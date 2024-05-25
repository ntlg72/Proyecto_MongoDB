package com.apiweb.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.Contiene;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.Talla;
import com.apiweb.backend.Repository.IOrdenesRepository;
import com.apiweb.backend.Repository.IProductosRepository;
import com.apiweb.backend.Repository.IUsuariosRepository;

public class OrdenesServiceImp implements IOrdenesService{

    @Autowired IOrdenesRepository ordenRepository;

    @Autowired IUsuariosRepository usuariosRepository;
    
    @Autowired IProductosRepository productoRepository;

    @Override
    public String guardarOrden(OrdenesModel orden) {
        int idUsuario = orden.getIdusuario(); // Obtiene el idUsuario de la orden
    
        // Verifica si el usuario existe
        Optional<UsuariosModel> usuarioOptional = usuariosRepository.findById(idUsuario);
        if (!usuarioOptional.isPresent()) {
            throw new RecursoNoEncontradoException("Error! El usuario con el Id " + idUsuario + " no existe en la base de datos.");
        }
    
        // Verifica si todos los productos de la orden existen y si la talla y cantidad están disponibles
        StringBuilder respuesta = new StringBuilder();
        boolean todosProductosExisten = true;
    
        for (Contiene contiene : orden.getContiene()) {
            Integer idProducto = contiene.getIdproducto();
            Optional<ProductosModel> productoOptional = productoRepository.findById(idProducto);
    
            if (!productoOptional.isPresent()) {
                respuesta.append("Error! El producto con el id ").append(idProducto).append(" no existe en la base de datos.\n");
                todosProductosExisten = false;
            } else {
                ProductosModel producto = productoOptional.get();
                String tallaSolicitada = contiene.getTalla();
                int cantidadSolicitada = contiene.getCantidad();
                boolean tallaEncontrada = false;
    
                // Verifica si la talla solicitada está disponible en el array de tallas
                for (Talla tallaProducto : producto.getTalla()) {
                    if (tallaProducto.getNombre().equals(tallaSolicitada)) {
                        tallaEncontrada = true;
                        if (tallaProducto.getCantidad() >= cantidadSolicitada) {
                            // Actualiza la cantidad de la talla en el producto si la orden está pagada
                            if (orden.getPago().isPagado()) {
                                tallaProducto.setCantidad(tallaProducto.getCantidad() - cantidadSolicitada);
                                productoRepository.save(producto);
                            }
                        } else {
                            respuesta.append("Error! El producto con el id ").append(idProducto)
                                    .append(" no tiene suficiente cantidad para la talla ").append(tallaSolicitada).append(".\n");
                            todosProductosExisten = false;
                        }
                    }
                }
    
                if (!tallaEncontrada) {
                    respuesta.append("Error! El producto con el id ").append(idProducto)
                            .append(" no tiene la talla ").append(tallaSolicitada).append(".\n");
                    todosProductosExisten = false;
                }
            }
        }
    
        // Si todos los productos existen y las tallas están disponibles, guarda la orden
        if (todosProductosExisten) {
            ordenRepository.save(orden);
            respuesta.append("La orden con el ID: ").append(orden.getId()).append(" fue creada con éxito.");
        } else {
            respuesta.append("No se puede crear la orden porque uno o más productos no existen o no tienen suficiente cantidad en la talla solicitada.");
        }
    
        return respuesta.toString().trim();
    }
    @Override
    public OrdenesModel buscarOrdenPorId(int idOrden) {
        Optional <OrdenesModel> ordenRecuperada = ordenRepository.findById(idOrden);
        return ordenRecuperada.orElseThrow(() -> new RecursoNoEncontradoException(
            "Error! La orden con el ID " + idOrden + " no fue encontrada."
        ));
    }

    @Override
    public List<OrdenesModel> listarOrdenes() {
        return ordenRepository.findAll();
    }
    
    //Actualizar Orden

    @Override
    public String actualizarOrden(int id, OrdenesModel ordenDetalles) {
    Optional<OrdenesModel> ordenExistenteOpt = ordenRepository.findById(id);
    if (!ordenExistenteOpt.isPresent()) {
        throw new RecursoNoEncontradoException("Error! La orden con el ID " + id + " no fue encontrada.");
    }

    OrdenesModel ordenExistente = ordenExistenteOpt.get();

    // Verificar si la orden esta pagada
    boolean estaPagado = ordenExistente.getPago().isPagado();

    if (estaPagado) {
        throw new RecursoNoEncontradoException("Error! No se puede actualizar la orden con el ID " + id + " debido a que ya está pagada.");
    }

    // Actualizar los detalles de la orden
    ordenExistente.setFechaorden(ordenDetalles.getFechaorden());
    ordenExistente.setContiene(ordenDetalles.getContiene());

    // Guardar los cambios en el repositorio
    ordenRepository.save(ordenExistente);

    return "Orden actualizada con exito";
    }

    @Override
    public void eliminarOrdenPorId(int idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new RecursoNoEncontradoException("Error! La orden con el ID " + idOrden + " no fue encontrada en la base de datos.");
        }
        ordenRepository.deleteById(idOrden);
    }
}




