let videojuegoSeleccionado = null;

// Cargar videojuegos al iniciar la página
document.addEventListener('DOMContentLoaded', function() {
    cargarVideojuegos();
    
    // Configurar el formulario
    document.getElementById('videojuegoForm').addEventListener('submit', function(e) {
        e.preventDefault();
        agregarVideojuego();
    });
    
    // Búsqueda en tiempo real
    document.getElementById('searchInput').addEventListener('keyup', function(e) {
        if (this.value.length >= 2 || this.value === '') {
            buscarVideojuegos();
        }
    });
});

// Cargar todos los videojuegos
async function cargarVideojuegos() {
    mostrarEstado('⏳ Cargando datos...');
    
    try {
        const response = await fetch('/api/videojuegos');
        const videojuegos = await response.json();
        
        mostrarVideojuegos(videojuegos);
        actualizarContador(videojuegos.length);
        mostrarEstado('✓ Datos cargados exitosamente');
        mostrarNotificacion(`Datos cargados: ${videojuegos.length} registros`, 'success');
    } catch (error) {
        mostrarEstado('✗ Error al cargar datos');
        mostrarNotificacion('Error al cargar los datos: ' + error.message, 'error');
    }
}

// Buscar videojuegos
async function buscarVideojuegos() {
    const termino = document.getElementById('searchInput').value.trim();
    
    if (termino === '') {
        cargarVideojuegos();
        return;
    }
    
    try {
        const response = await fetch(`/api/videojuegos/buscar?q=${encodeURIComponent(termino)}`);
        const videojuegos = await response.json();
        
        mostrarVideojuegos(videojuegos);
        actualizarContador(videojuegos.length);
    } catch (error) {
        mostrarNotificacion('Error al buscar: ' + error.message, 'error');
    }
}

// Mostrar videojuegos en la tabla
function mostrarVideojuegos(videojuegos) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';
    
    videojuegos.forEach(v => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-id', v.videojuegoID);
        tr.onclick = () => seleccionarVideojuego(v);
        
        tr.innerHTML = `
            <td>${v.videojuegoID}</td>
            <td>${v.titulo}</td>
            <td>${v.genero || '-'}</td>
            <td>${v.plataforma || '-'}</td>
            <td>${v.stock}</td>
            <td>$${v.precio.toFixed(2)}</td>
            <td>
                <button class="action-btn btn-edit" onclick="event.stopPropagation(); seleccionarVideojuego(${JSON.stringify(v).replace(/"/g, '&quot;')})">✏️ Editar</button>
                <button class="action-btn btn-delete" onclick="event.stopPropagation(); seleccionarYEliminar(${v.videojuegoID})">🗑️</button>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

// Seleccionar videojuego
function seleccionarVideojuego(videojuego) {
    videojuegoSeleccionado = videojuego;
    
    // Remover selección anterior
    document.querySelectorAll('#tableBody tr').forEach(tr => tr.classList.remove('selected'));
    
    // Marcar nueva selección
    const tr = document.querySelector(`#tableBody tr[data-id="${videojuego.videojuegoID}"]`);
    if (tr) tr.classList.add('selected');
    
    // Llenar formulario
    document.getElementById('videojuegoID').value = videojuego.videojuegoID;
    document.getElementById('titulo').value = videojuego.titulo;
    document.getElementById('genero').value = videojuego.genero || '';
    document.getElementById('plataforma').value = videojuego.plataforma || '';
    document.getElementById('stock').value = videojuego.stock;
    document.getElementById('precio').value = videojuego.precio;
    
    // Habilitar botones
    document.getElementById('btnActualizar').disabled = false;
    document.getElementById('btnEliminar').disabled = false;
}

// Agregar videojuego
async function agregarVideojuego() {
    const videojuego = {
        titulo: document.getElementById('titulo').value.trim(),
        genero: document.getElementById('genero').value.trim(),
        plataforma: document.getElementById('plataforma').value.trim(),
        stock: parseInt(document.getElementById('stock').value),
        precio: parseFloat(document.getElementById('precio').value)
    };
    
    if (!validarVideojuego(videojuego)) return;
    
    try {
        const response = await fetch('/api/videojuegos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(videojuego)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            mostrarNotificacion('Videojuego agregado exitosamente', 'success');
            mostrarEstado('✓ Nuevo videojuego agregado');
            limpiarFormulario();
            cargarVideojuegos();
        } else {
            mostrarNotificacion(result.message || 'Error al agregar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al agregar: ' + error.message, 'error');
    }
}

// Actualizar videojuego
async function actualizarVideojuego() {
    if (!videojuegoSeleccionado) {
        mostrarNotificacion('Selecciona un videojuego para actualizar', 'warning');
        return;
    }
    
    const videojuego = {
        titulo: document.getElementById('titulo').value.trim(),
        genero: document.getElementById('genero').value.trim(),
        plataforma: document.getElementById('plataforma').value.trim(),
        stock: parseInt(document.getElementById('stock').value),
        precio: parseFloat(document.getElementById('precio').value)
    };
    
    if (!validarVideojuego(videojuego)) return;
    
    try {
        const response = await fetch(`/api/videojuegos/${videojuegoSeleccionado.videojuegoID}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(videojuego)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            mostrarNotificacion('Videojuego actualizado exitosamente', 'success');
            mostrarEstado('✓ Registro actualizado');
            limpiarFormulario();
            cargarVideojuegos();
        } else {
            mostrarNotificacion(result.message || 'Error al actualizar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al actualizar: ' + error.message, 'error');
    }
}

// Seleccionar y eliminar
function seleccionarYEliminar(id) {
    fetch(`/api/videojuegos/${id}`)
        .then(response => response.json())
        .then(videojuego => {
            videojuegoSeleccionado = videojuego;
            eliminarVideojuego();
        });
}

// Eliminar videojuego
async function eliminarVideojuego() {
    if (!videojuegoSeleccionado) {
        mostrarNotificacion('Selecciona un videojuego para eliminar', 'warning');
        return;
    }
    
    if (!confirm(`¿Estás seguro de eliminar el videojuego:\n"${videojuegoSeleccionado.titulo}"?\n\nEsta acción no se puede deshacer.`)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/videojuegos/${videojuegoSeleccionado.videojuegoID}`, {
            method: 'DELETE'
        });
        
        const result = await response.json();
        
        if (response.ok) {
            mostrarNotificacion('Videojuego eliminado exitosamente', 'success');
            mostrarEstado('✓ Registro eliminado');
            limpiarFormulario();
            cargarVideojuegos();
        } else {
            mostrarNotificacion(result.message || 'Error al eliminar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al eliminar: ' + error.message, 'error');
    }
}

// Limpiar formulario
function limpiarFormulario() {
    document.getElementById('videojuegoForm').reset();
    document.getElementById('videojuegoID').value = '';
    document.getElementById('searchInput').value = '';
    document.getElementById('btnActualizar').disabled = true;
    document.getElementById('btnEliminar').disabled = true;
    videojuegoSeleccionado = null;
    
    // Remover selección de la tabla
    document.querySelectorAll('#tableBody tr').forEach(tr => tr.classList.remove('selected'));
}

// Validar videojuego
function validarVideojuego(videojuego) {
    if (!videojuego.titulo) {
        mostrarNotificacion('El título es obligatorio', 'warning');
        return false;
    }
    
    if (isNaN(videojuego.stock) || videojuego.stock < 0) {
        mostrarNotificacion('El stock debe ser un número válido', 'warning');
        return false;
    }
    
    if (isNaN(videojuego.precio) || videojuego.precio < 0) {
        mostrarNotificacion('El precio debe ser un número válido', 'warning');
        return false;
    }
    
    return true;
}

// Actualizar contador
function actualizarContador(total) {
    document.getElementById('contador').textContent = `📊 Total de registros: ${total}`;
}

// Mostrar estado
function mostrarEstado(mensaje) {
    document.getElementById('estado').textContent = mensaje;
}

// Mostrar notificación
function mostrarNotificacion(mensaje, tipo) {
    const notification = document.getElementById('notification');
    notification.textContent = mensaje;
    notification.className = `notification ${tipo}`;
    notification.style.display = 'block';
    
    setTimeout(() => {
        notification.style.display = 'none';
    }, 3000);
}
