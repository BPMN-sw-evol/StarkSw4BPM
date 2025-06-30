import streamlit as st
import requests

st.set_page_config(page_title="Cargar BPMN", page_icon="⚙️")

st.title("Cargar archivo BPMN")
st.write("Sube un archivo `.bpmn` para enviarlo al backend.")

uploaded_file = st.file_uploader("Selecciona un archivo BPMN", type=["bpmn"])

# Control de sesión
if "bpmn_procesado" not in st.session_state:
    st.session_state.bpmn_procesado = False
if "tareas" not in st.session_state:
    st.session_state.tareas = {}

if uploaded_file:
    st.success(f"Archivo seleccionado: {uploaded_file.name}")

    if st.button("Enviar al backend"):
        try:
            files = {"file": (uploaded_file.name, uploaded_file, "application/xml")}
            response = requests.post("http://localhost:8080/api/bpmn/upload", files=files)

            if response.status_code == 200:
                st.success("✅ Archivo enviado y procesado correctamente.")
                st.session_state.bpmn_procesado = True
            else:
                st.error(f"❌ Error desde el backend: {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ No se pudo conectar con el backend en http://localhost:8080")

if st.session_state.bpmn_procesado:
    if st.button("Ver tareas extraídas del BPMN"):
        try:
            response = requests.get("http://localhost:8080/api/bpmn/tasks")
            if response.status_code == 200:
                data = response.json()
                st.session_state.tareas = data
            else:
                st.error(f"❌ Error al obtener tareas: {response.status_code} - {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ No se pudo conectar al backend de tareas en http://localhost:8080")

# Si ya se cargaron las tareas, mostrar la configuración
if st.session_state.tareas:
    tareas = st.session_state.tareas

    st.subheader("🛠️ Configuración de tareas")

    # USER TASKS
    st.markdown("### 👤 User Tasks")
    user_config = {}
    for tarea in tareas.get("userTask", []):
        opcion = st.selectbox(
            f"Tipo de tarea para: {tarea}",
            ["Formulario", "Lista", "Aprobación"],
            key=f"user_{tarea}"
        )
        user_config[tarea] = opcion

    # SERVICE TASKS
    st.markdown("### ⚙️ Service Tasks")
    service_config = {}
    for tarea in tareas.get("serviceTask", []):
        opcion = st.selectbox(
            f"Tipo de operación para: {tarea}",
            ["CRUD", "Solo consulta", "Actualizar"],
            key=f"service_{tarea}"
        )
        service_config[tarea] = opcion

    # SEND TASKS
    st.markdown("### 📤 Send Tasks")
    send_config = {}
    for tarea in tareas.get("sendTask", []):
        st.markdown(f"**{tarea}**")
        destino = st.text_input("¿A quién va dirigido?", key=f"send_to_{tarea}")
        contenido = st.text_input("¿Qué información se envía?", key=f"send_what_{tarea}")
        send_config[tarea] = {"destinatario": destino, "contenido": contenido}

    # Paso 1: Definir flag de sesión para saber si ya se guardó
    if "config_guardada" not in st.session_state:
        st.session_state.config_guardada = False
    if "ultima_config" not in st.session_state:
        st.session_state.ultima_config = {}

    # Paso 2: Guardar la configuración al presionar el botón
    if st.button("Guardar configuración de tareas"):
        configuracion_final = {
            "userTasks": user_config,
            "serviceTasks": service_config,
            "sendTasks": send_config
        }

        try:
            response = requests.post("http://localhost:8080/api/tasks/import", json=configuracion_final)

            if response.status_code == 200:
                st.session_state.config_guardada = True
                st.session_state.ultima_config = configuracion_final
                st.success("✅ Configuración guardada en base de datos correctamente.")
            else:
                st.error(f"❌ Error guardando configuración: {response.status_code} - {response.text}")
        except requests.exceptions.ConnectionError:
            st.error("❌ No se pudo conectar al backend.")

    # Paso 3: Si ya se guardó, mostrar el JSON y el botón de generar
    if st.session_state.config_guardada:
        st.subheader("📝 Última configuración guardada")
        st.json(st.session_state.ultima_config)

        if st.button("🚀 Generar proyecto desde configuración"):
            try:
                gen_response = requests.post("http://localhost:8080/api/generator/generate-from-config")
                if gen_response.status_code == 200:
                    st.success("✅ Proyecto generado exitosamente.")
                    st.write(gen_response.text)

                    download_url = "http://localhost:8080/api/generator/download-zip"
                    st.markdown(f"📦 [Haz clic aquí para descargar el proyecto .zip]({download_url})")
                else:
                    st.error(f"❌ Error generando proyecto: {gen_response.status_code} - {gen_response.text}")
            except requests.exceptions.ConnectionError:
                st.error("❌ No se pudo conectar al backend para generar el proyecto.")

