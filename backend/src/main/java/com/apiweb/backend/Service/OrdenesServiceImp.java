package com.apiweb.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiweb.backend.Exception.RecursoNoEncontradoException;
import com.apiweb.backend.Model.Contiene;
import com.apiweb.backend.Model.OrdenesModel;
import com.apiweb.backend.Model.ProductosModel;
import com.apiweb.backend.Model.Talla;
import com.apiweb.backend.Repository.IOrdenesRepository;
import com.apiweb.backend.Repository.IProductosRepository;
import com.apiweb.backend.Repository.IUsuariosRepository;

@Service

public class OrdenesServiceImp implements IOrdenesService{

    @Autowired IOrdenesRepository ordenRepository;

    @Autowired IUsuariosRepository usuariosRepository;
    
    @Autowired IProductosRepository productoRepository;

    @Autowired ProductosServiceImp productoService;

    @Override
    public String guardarOrden(OrdenesModel orden) {
        int idUsuario = orden.getIdusuario(); // Obtiene el idUsuario de la orden
        
        // Verifica si el usuario existe
        usuariosRepository.findById(idUsuario)
            .orElseThrow(() -> new RecursoNoEncontradoException("Error! El usuario con el Id " + idUsuario + " no existe en la base de datos."));
    
        // Verifica si todos los productos de la orden existen y si la talla y cantidad están disponibles
        StringBuilder respuesta = new StringBuilder();
        boolean todosProductosExisten = true;
        double valorTotalOrden = 0;
    
        for (Contiene contiene : orden.getContiene()) {
            Integer idProducto = contiene.getIdproducto();
            ProductosModel producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Error! El producto con el id " + idProducto + " no existe en la base de datos."));
    
            String tallaSolicitada = contiene.getTalla();
            int cantidadSolicitada = contiene.getCantidad();
            Talla tallaProducto = producto.getTalla().stream()
                    .filter(talla -> talla.getNombre().equals(tallaSolicitada))
                    .findFirst()
                    .orElse(null);
    
            if (tallaProducto == null || tallaProducto.getCantidad() < cantidadSolicitada) {
                respuesta.append("Error! El producto con el id ").append(idProducto);
                if (tallaProducto == null) {
                    respuesta.append(" no tiene la talla ").append(tallaSolicitada).append(".\n");
                } else {
                    respuesta.append(" no tiene suficiente cantidad para la talla ").append(tallaSolicitada).append(".\n");
                }
                todosProductosExisten = false;
            }
            
            // Sumar el precio del producto al valor total de la orden
            valorTotalOrden += cantidadSolicitada * producto.getPrecio();
        }
    
        // Si todos los productos existen y las tallas están disponibles, guarda la orden
        if (todosProductosExisten) {
            // Verifica si la orden está pagada
            if (orden.getPago() != null && orden.getPago().isPagado()) {
                // Actualiza el valor de pago en la orden
                orden.setSubtotal(valorTotalOrden);
                
                // Guarda la orden en el repositorio
                ordenRepository.save(orden);
                respuesta.append("La orden con el ID: ").append(orden.getId()).append(" fue creada con éxito.");
                
                // Actualiza las cantidades de los productos
                for (Contiene contiene : orden.getContiene()) {
                Integer idProducto = contiene.getIdproducto();
                ProductosModel producto = productoRepository.findById(idProducto).get(); 
                String tallaSolicitada = contiene.getTalla();
                int cantidadSolicitada = contiene.getCantidad();
                Talla tallaProducto = producto.getTalla().stream()
                    .filter(talla -> talla.getNombre().equals(tallaSolicitada))
                    .findFirst()
                    .orElse(null);

            if (tallaProducto != null && tallaProducto.getCantidad() >= cantidadSolicitada) {
                tallaProducto.setCantidad(tallaProducto.getCantidad() - cantidadSolicitada);
                productoRepository.save(producto);
            }
        }

            } else {
                // Actualiza el valor de pago en la orden
                orden.setSubtotal(valorTotalOrden);
                
                // Guarda la orden en el repositorio
                ordenRepository.save(orden);
                respuesta.append("La orden con el ID: ").append(orden.getId()).append(" fue creada con éxito.");
            }
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
    @Transactional
    public String actualizarOrden(int id, int idusuario, OrdenesModel ordenDetalles) {
    // Verificar si el usuario existe
    usuariosRepository.findById(idusuario)
        .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idusuario + " no fue encontrado en la BD."));

    // Buscar la orden en la base de datos
    OrdenesModel orden = ordenRepository.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Error! La orden con el ID " + id + " no fue encontrada en la BD."));

    // Verificar si el usuario que intenta actualizar la orden es el mismo que la creó
    if (idusuario != orden.getIdusuario()) {
        throw new RecursoNoEncontradoException("Error! Solo el usuario que creó la orden puede actualizarla.");
    }
    // verifica si la orde esta paga si esta paga no se puede actulizar, si la orden no esta paga si deja actualizar 
    boolean estaPagado = orden.getPago() != null && orden.getPago().isPagado();
    if (estaPagado) {
        throw new RecursoNoEncontradoException("Error! No se puede actualizar la orden con el ID " + id + " porque ya está pagada.");
    }

    StringBuilder respuesta = new StringBuilder();
    boolean todosProductosExisten = true;
    double valorTotalOrden = 0;

    // Verificar productos y tallas 
    for (Contiene contiene : ordenDetalles.getContiene()) {
        Integer idProducto = contiene.getIdproducto();
        ProductosModel producto = productoRepository.findById(idProducto)
            .orElseThrow(() -> new RecursoNoEncontradoException(new StringBuilder()
                .append("Error! El producto con el id ")
                .append(idProducto)
                .append(" no existe en la base de datos.")
                .toString()));

        String tallaSolicitada = contiene.getTalla();
        int cantidadSolicitada = contiene.getCantidad();
        Talla tallaProducto = producto.getTalla().stream()
            .filter(talla -> talla.getNombre().equals(tallaSolicitada))
            .findFirst()
            .orElse(null);

        if (tallaProducto == null || tallaProducto.getCantidad() < cantidadSolicitada) {
            respuesta.append("Error! El producto con el id ").append(idProducto);
            if (tallaProducto == null) {
                respuesta.append(" no tiene la talla ").append(tallaSolicitada).append(".\n");
            } else {
                respuesta.append(" no tiene suficiente cantidad para la talla ").append(tallaSolicitada).append(".\n");
            }
            todosProductosExisten = false;
        } else {
            // Acumula el subtotal
            valorTotalOrden += cantidadSolicitada * producto.getPrecio();
        }
    }

    if (todosProductosExisten) {
        // Actualiza la orden y el subtotal y guarda
        orden.setContiene(ordenDetalles.getContiene());
        orden.setSubtotal(valorTotalOrden);
        ordenRepository.save(orden);

        respuesta.append("La orden con el ID ").append(id).append(" fue actualizada con éxito.");
    } else {
        respuesta.append("No se pudo actualizar la orden con el ID ").append(id).append(" debido a que uno o más productos no existen en la BD.");
    }

    return respuesta.toString().trim();
    }

    @Override
    public void eliminarOrdenPorId(int idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new RecursoNoEncontradoException("Error! La orden con el ID " + idOrden + " no fue encontrada en la base de datos.");
        }
        ordenRepository.deleteById(idOrden);
    }

}




