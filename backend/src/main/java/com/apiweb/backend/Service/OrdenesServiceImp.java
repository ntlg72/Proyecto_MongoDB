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
import com.apiweb.backend.Model.TopProductos;
import com.apiweb.backend.Model.TopUsuariosCompras;
import com.apiweb.backend.Model.UsuariosModel;
import com.apiweb.backend.Model.ENUM.TipoUsuario;
import com.apiweb.backend.Model.Talla;
import com.apiweb.backend.Model.TopProductomenos;
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
    public String guardarOrden(OrdenesModel orden, String username) {
    
        try {
            int idUsuario = orden.getIdusuario(); // Obtiene el idUsuario de la orden
            
            // Verifica si el usuario existe y es cliente
            UsuariosModel usuario = usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error! El usuario con el Id " + idUsuario + " no existe en la base de datos."));
            
            boolean esCliente = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getUsername().equals(username) && cuenta.getTipousuario() == TipoUsuario.cliente);
                
            if (!esCliente) {
                throw new RecursoNoEncontradoException("Error! Solo los clientes pueden crear ordenes.");
            }
            
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
        
                if (cantidadSolicitada <= 0) {
                    throw new IllegalArgumentException("Error! La cantidad solicitada para el producto con el id " + idProducto + " debe ser mayor a 0.");
                }
        
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
                }
    
                // Guarda la orden en el repositorio
                OrdenesModel ordenGuardada = ordenRepository.save(orden);
                
                // Actualiza las cantidades de los productos si la orden está pagada
                if (orden.getPago() != null && orden.getPago().isPagado()) {
                    for (Contiene contiene : orden.getContiene()) {
                        Integer idProducto = contiene.getIdproducto();
                        ProductosModel producto = productoRepository.findById(idProducto).orElseThrow(); 
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
                }
                respuesta.append("La orden con el ID: ").append(ordenGuardada.getId()).append(" fue creada con éxito.");
            } else {
                respuesta.append("No se puede crear la orden porque uno o más productos no existen o no tienen suficiente cantidad en la talla solicitada.");
            }
        
            return respuesta.toString().trim();
        } catch (RecursoNoEncontradoException | IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error inesperado al guardar la orden: " + e.getMessage();
        }
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
    public String actualizarOrden(int id, int idusuario, String username, OrdenesModel ordenDetalles) {
    
        try {
            // Verificar si el usuario existe y es cliente
            UsuariosModel usuario = usuariosRepository.findById(idusuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: El usuario con el Id " + idusuario + " no fue encontrado en la BD."));
    
            boolean esCliente = usuario.getCuentas().stream()
                .anyMatch(cuenta -> cuenta.getUsername().equals(username) && cuenta.getTipousuario() == TipoUsuario.cliente);
    
            if (!esCliente) {
                throw new RecursoNoEncontradoException("Error: Solo los clientes pueden crear órdenes.");
            }
    
            // Verificar si la orden existe y pertenece al usuario
            OrdenesModel orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error: La orden con el ID " + id + " no fue encontrada en la BD."));
    
            if (idusuario != orden.getIdusuario()) {
                throw new RecursoNoEncontradoException("Error: Solo el usuario que creó la orden puede actualizarla.");
            }
    
            // Verificar si la orden ya está pagada
            if (orden.getPago() != null && orden.getPago().isPagado()) {
                throw new RecursoNoEncontradoException("Error: No se puede actualizar la orden con el ID " + id + " porque ya está pagada.");
            }
    
            StringBuilder respuesta = new StringBuilder();
            double valorTotalOrden = 0;
            boolean todosProductosExisten = true; // Variable para verificar si todos los productos existen
    
            // Verificar productos y tallas
            for (Contiene contiene : ordenDetalles.getContiene()) {
                Integer idProducto = contiene.getIdproducto();
                ProductosModel producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Error: El producto con el id " + idProducto + " no existe en la base de datos."));
    
                String tallaSolicitada = contiene.getTalla();
                int cantidadSolicitada = contiene.getCantidad();
    
                // Verificar que la cantidad solicitada no sea igual o menor a 0
                if (cantidadSolicitada <= 0) {
                    return "Error: La cantidad solicitada para el producto con el id " + idProducto + " debe ser mayor a 0.";
                }
    
                Talla tallaProducto = producto.getTalla().stream()
                    .filter(talla -> talla.getNombre().equals(tallaSolicitada))
                    .findFirst()
                    .orElse(null);
    
                if (tallaProducto == null || tallaProducto.getCantidad() < cantidadSolicitada) {
                    respuesta.append("Error: El producto con el id ").append(idProducto);
                    if (tallaProducto == null) {
                        respuesta.append(" no tiene la talla ").append(tallaSolicitada).append(".\n");
                    } else {
                        respuesta.append(" no tiene suficiente cantidad para la talla ").append(tallaSolicitada).append(".\n");
                    }
                    todosProductosExisten = false; // Si un producto no existe, establece la variable a false
                } else {
                    // Acumula el subtotal
                    valorTotalOrden += cantidadSolicitada * producto.getPrecio();
                }
            }
    
            // Si todos los productos existen, actualiza la orden y el subtotal y guarda
            if (todosProductosExisten) {
                orden.setContiene(ordenDetalles.getContiene());
                orden.setSubtotal(valorTotalOrden);
                ordenRepository.save(orden);
    
                respuesta.append("La orden con el ID ").append(id).append(" fue actualizada con éxito.");
            } else {
                respuesta.append("No se pudo actualizar la orden con el ID ").append(id).append(" debido a que uno o más productos no existen en la BD.");
            }
    
            return respuesta.toString().trim();
        } catch (RecursoNoEncontradoException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Ocurrió un error inesperado: " + e.getMessage();
        }
    }

    @Override
    public void eliminarOrdenPorId(int idOrden) {
        if (!ordenRepository.existsById(idOrden)) {
            throw new RecursoNoEncontradoException("Error! La orden con el ID " + idOrden + " no fue encontrada en la base de datos.");
        }
        ordenRepository.deleteById(idOrden);
    }

    @Override
    public List<TopProductos> getTop3ProductosMasVendidos() {
        return ordenRepository.findTop3ProductosMasVendidos();
    }

    @Override
    public List<TopUsuariosCompras> topUsuarioConMasCompras(){
        return ordenRepository.topUsuarioConMasCompras();
    }
    
    @Override
    public List<TopProductomenos> getTop3ProductosMenosVendidos(){
         return ordenRepository.getTop3ProductosMenosVendidos();
    }


}




