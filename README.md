````markdown
# StarkSw4BPM

Generador de software basado en procesos de negocio (BPM). A partir de un modelo `.bpmn`, esta herramienta crea automáticamente un proyecto Spring Boot ejecutable con Camunda y una interfaz amigable en Streamlit para facilitar la configuración.

---

## 🧰 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **Camunda BPM**
- **Python 3.9+**
- **Streamlit**
- **Base de datos H2**
- **Maven**

---

## ✅ Requisitos Previos

Asegúrate de tener instalados los siguientes programas:

- [Git](https://git-scm.com/)
- [Java JDK 17 o superior](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Python 3.9+](https://www.python.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended IDE for Java development)

---

## 🚀 Instrucciones paso a paso

### 1️⃣ Clonar el repositorio

Abre **Git Bash** (clic derecho en el escritorio o en una carpeta donde quieras que quede el proyecto  y selecciona *"Open Git Bash Here"*) y escribe:

```bash
git clone https://github.com/BPMN-sw-evol/StarkSw4BPM.git
````

Este comando descargará una copia del proyecto desde GitHub.

Luego accede a la carpeta del proyecto:

```bash
cd StarkSw4BPM
```

Este comando te mueve dentro del directorio del proyecto que acabas de clonar.

---

### 2️⃣ Ejecutar el backend (Spring Boot)

Ahora accede a la carpeta donde está la aplicación backend:

```bash 
cd bpmGenerator
```

Este directorio contiene el código Java con Spring Boot y Camunda.

#### Compila y ejecuta el backend:

```bash 
mvn clean install
```

```bash
mvn spring-boot:run
```

🔗 Esto inicia el servidor en: [http://localhost:8080](http://localhost:8080)

> ⚠️ **Importante:** Esta aplicación backend funciona únicamente como **API REST**.
> Para poder usarla e interactuar con ella (por ejemplo, cargar modelos BPMN o generar código), **es recomendado continuar con los pasos para ejecutar la interfaz de usuario (UI)**.

---

### 3️⃣ Ejecutar la Interfaz de Usuario (Streamlit)

⚠️ Este paso debe hacerse en una **terminal con permisos de administrador**. Para abrirla:

1. Pulsa `Inicio`
2. Escribe `cmd`
3. Haz clic derecho y selecciona **"Ejecutar como administrador"**

#### Luego sigue estos pasos:

1. Navega a la carpeta de la interfaz:

```bash (Busca la ruta donde clonaste el repositorio)
cd ruta\completa\hasta\StarkSw4BPM\UI
```

2. Crea un entorno virtual (solo la primera vez):

```bash
py -m venv env
```

3. Activa el entorno virtual:

```bash
.\env\Scripts\activate
```

4. Instala las dependencias:

```bash
pip install -r requirements.txt
```

5. Ejecuta la interfaz:

```bash
streamlit run app.py
```

🔗 Esto abrirá la interfaz en tu navegador en: [http://localhost:8501](http://localhost:8501)

---

## 🧪 Verificación

Cuando ambos servicios estén funcionando correctamente deberías tener:

* Backend API corriendo en: `http://localhost:8080`
* Interfaz de usuario en: `http://localhost:8501`

---

## 📂 Proyectos generados

Cada vez que generas una aplicación, el resultado se guarda dentro de:

```
StarkSw4BPM/generatedProjects/
```

---
