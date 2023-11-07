# YA-C-Grupo1-Parcial-TP3-Dogs

<p>Parcial de la materia Taller de Programación 3, ORT. Año 2023.</p>

## Integrantes
<ul>
    <li>Arnold Amorin Arteaga (arnoldamorin)</li>
    <li>Nelson Fernandez (Nelo-Fernandez)</li>
    <li>Francisco Lagares García (Pancholag)</li>
    <li>Federico Kolonski (fedekolo)</li>
    <li>Matías Rosenstein (MatiRosen)</li>
    <li>Julia Valdovinos (julia-valdo)</li>
</ul>

## Dog-It

<p>
En esta oportunidad desarrollamos una app soportada para el SO Android con kotlin para adopción de mascotas. En nuestro caso, solo de perros, utilizando la api pública de puppies (https://dog.ceo/dog-api/documentation/random)
</p>

<p>La app cuenta con un tabBar con distintas secciones:</p>
<ul>
    <li>Home (listado de perritos a adoptar).</li>
    <li>Listado de favoritos.</li>
    <li>Adopciones (listado de adopciones realizadas).</li>
    <li>Crear publicación (en esta sección se crea una nueva publicación de adopción y al guardarla, se resetea la vista y queda disponible para crear otra).</li>
</ul>

<p>Al crear una publicación, se despliega un toast de notificación con texto "¡Publicación creada con éxito!".</p>

<p>También existe opción de menú desplegable de izquierda a derecha con la
sección Perfil y Configuración: tener la app en modo claro u oscuro.</p>

<p>La información que tiene el usuario es imagen, teléfono, nombre y apellido.</p>

<p>Como usuario, se puede observar el listado de publicaciones de perros que
están en adopción.</p>
<ul>
    <li>Dicho listado tiene filtros por raza, ubicación y posibilidad de ordenarse por fecha de publicación.</li>
    <li>El listado considera además un campo searchBar, donde puedo buscar por raza y por subraza.</li>
    <li>Los filtros pueden anidarse.</li>
</ul>

<p>La celda cuenta con una imagen, nombre, raza, subraza, edad, sexo y la posibilidad de agregar dicho perrito a mi lista de favoritos.</p>

<p>A su vez, al seleccionar dicha celda, ingreso a la ficha completa y detallada con la siguiente información:</p>
<ul>
    <li>Raza y subraza si así tuviese.</li>
    <li>Nombre del perrito.</li>
    <li>Edad.</li>
    <li>Si es macho o hembra.</li>
    <li>Descripción.</li>
    <li>Peso.</li>
    <li>Ubicación (se contemplan todas las provincias Argentinas).</li>
    <li>Imágenes representativas del perrito, hasta 5 imágenes, scrollables y seleccionables para poder verlas en tamaño grande.</li>
    <li>Nombre del dueño, y button actions para llamar o mandar mensajes. El ícono de llamada abre el panel de llamada del celular. El de mensajes se deja para una siguiente iteración.</li>
    <li>Un campo con observaciones de la persona de tránsito del perrito.</li>
</ul>

<p>Desde esta sección de detalle, puedo también agregar o quitar de favoritos y también puedo adoptar dicho perrito. Esto significa que una vez adoptado, la publicación de adopción queda en estado ‘Adoptado’ y no debe mostrarse en el listado de la home, pero debe actualizarse en el listado de Adopciones.</p>

<p>Al adoptar un perrito, se despliega un toast de notificación con texto
"Felicitaciones por adoptar a {dogName}". La adopción se persiste en Firebase y se elimina la publicación del listado de la home.</p>

## Dependencias
<ul>
    <li>Navigation</li>
    <li>SwipeRefreshLayout</li>    
    <li>Firebase</li>
    <ul>
        <li>Firestore Database</li>
        <li>Authentication</li>
    </ul>
    <li>Glide</li>
    <li>CircularImageView</li>
    <li>Preference</li>
    <li>Retrofit</li>
    <li>Coroutines</li>
</ul>

## Preguntas:
### 1) En el caso que se pida extender la app para otros tipos de mascotas, por ejemplo gatos, ¿la app es flexible? ¿Qué cambios realizarían?
Si es extendible, pero habría que hacer modificaciones. Por ejemplo, habría que agregar compatibilidad con la API de la mascota específica para obtener la raza y subraza. También habría que agregar un campo para el tipo de mascota en la base de datos de Firebase, si es que queremos filtrar por tipo de mascota.

### 2) ¿Qué tipo de arquitectura usaron y por qué? ¿Podría mejorarla?
Utilizamos Kotlin con una arquitectura MVC. Creemos que se podría mejorar utilizando MVVM, para acceder a retrofit desde un ViewModel, pero no llegamos a implementarlo.

### 3) ¿Tuvieron fugas o retención de memoria? ¿Qué consideraciones tuvieron en cuenta?
No encontramos fugas ni retenciones de memoria. Sin embargo, para evitarlo tuvimos recaudos como utilizar el método onDestroy() para liberar recursos, y utilizar el método onStop() para liberar la memoria de los recursos que no se están utilizando.

### 4) ¿Qué mejoras detectan que podrían realizarle a la app?
Se podría agregar el acceso a la API desde un ViewModel, para cargar las cosas una sola vez con retrofit. No nos dió el tiempo para realizar esto, pero sabemos que es posible.