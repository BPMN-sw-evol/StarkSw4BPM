import os
import streamlit.web.cli as stcli
import sys

# Configuración forzada de Streamlit para este entorno
os.environ["STREAMLIT_SERVER_PORT"] = "8501"
os.environ["STREAMLIT_SERVER_HEADLESS"] = "true"
os.environ["STREAMLIT_SERVER_ENABLE_CORS"] = "false"
os.environ["STREAMLIT_GLOBAL_DEVELOPMENTMODE"] = "false"

# Ejecutar la app
sys.argv = ["streamlit", "run", "app.py"]
sys.exit(stcli.main())