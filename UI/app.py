import streamlit as st
import requests
import io

st.set_page_config(page_title="Upload BPMN", page_icon="⚙️")

st.title("Upload BPMN File")
st.write("Upload a `.bpmn` file to send it to the backend.")

uploaded_file = st.file_uploader("Select a BPMN file", type=["bpmn"])

# Session state control
if "bpmn_processed" not in st.session_state:
    st.session_state.bpmn_processed = False
if "tasks" not in st.session_state:
    st.session_state.tasks = {}

if uploaded_file:
    st.success(f"Selected file: {uploaded_file.name}")

    if st.button("Process file"):
        try:
            files = {"file": (uploaded_file.name, uploaded_file, "application/xml")}
            response = requests.post("http://localhost:8080/api/bpmn/upload", files=files)

            if response.status_code == 200:
                st.success("✅ File successfully sent and processed.")
                st.session_state.bpmn_processed = True
            else:
                st.error(f"❌ Backend error: {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ Could not connect to backend at http://localhost:8080")

if st.session_state.bpmn_processed:
    if st.button("View extracted tasks from BPMN"):
        try:
            response = requests.get("http://localhost:8080/api/bpmn/tasks")
            if response.status_code == 200:
                data = response.json()
                st.session_state.tasks = data
            else:
                st.error(f"❌ Error fetching tasks: {response.status_code} - {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ Could not connect to task backend at http://localhost:8080")

# If tasks are already loaded, show the configuration
if st.session_state.tasks:
    tasks = st.session_state.tasks

    st.subheader("🛠️ Task Configuration")

    # USER TASKS
    st.markdown("### 👤 User Tasks")
    user_config = {}
    for task in tasks.get("userTask", []):
        option = st.selectbox(
            f"Form type for: {task}",
            ["Camunda Forms", "Embedded or External Task Forms", "Generated Task Forms"],
            key=f"user_{task}"
        )
        user_config[task] = option

    # SERVICE TASKS
    st.markdown("### ⚙️ Service Tasks")
    service_config = {}
    for task in tasks.get("serviceTask", []):
        option = st.selectbox(
            f"Implementation for: {task}",
            ["External", "Java class", "Expression", "Delegate expression", "Connector"],
            key=f"service_{task}"
        )
        service_config[task] = option

    # SEND TASKS
    st.markdown("### 📤 Send Tasks")
    send_config = {}
    for task in tasks.get("sendTask", []):
        option = st.selectbox(
            f"Implementation for: {task}",
            ["External", "Java class", "Expression", "Delegate expression", "Connector"],
            key=f"send_{task}"
        )
        send_config[task] = option

    # Step 1: Session flag to know if config is saved
    if "config_saved" not in st.session_state:
        st.session_state.config_saved = False
    if "last_config" not in st.session_state:
        st.session_state.last_config = {}

    # Step 2: Save the configuration
    if st.button("Save task configuration"):
        final_config = {
            "userTasks": user_config,
            "serviceTasks": service_config,
            "sendTasks": send_config
        }

        try:
            response = requests.post("http://localhost:8080/api/tasks/import", json=final_config)

            if response.status_code == 200:
                st.session_state.config_saved = True
                st.session_state.last_config = final_config
            else:
                st.error(f"❌ Error saving configuration: {response.status_code} - {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ Could not connect to the backend.")

    # Step 3: Generate project
    if st.session_state.config_saved:
        st.success("✅ Task configuration successfully saved.")

        if st.button("🚀 Generate project from configuration"):
            try:
                gen_response = requests.post("http://localhost:8080/api/generator/generate-from-config")
                if gen_response.status_code == 200:
                    zip_response = requests.get("http://localhost:8080/api/generator/download-zip")
                    if zip_response.status_code == 200:
                        st.success("✅ Project generated.")

                        st.download_button(
                            label="📦 Download generated project",
                            data=io.BytesIO(zip_response.content),
                            file_name="generated-project.zip",
                            mime="application/zip"
                        )
                    else:
                        st.error("❌ Error downloading the .zip file")
                else:
                    st.error(f"❌ Error generating project: {gen_response.status_code} - {gen_response.text}")
            except requests.exceptions.ConnectionError:
                st.error("❌ Could not connect to the backend to generate the project.")
