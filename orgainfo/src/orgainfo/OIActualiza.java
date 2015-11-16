package orgainfo;

/**
 * Interfaz para objetos que eventualmente pueden ser actualizados
 * de una manera persistente.
 */
interface OIActualiza {
    void open() throws OIExcepcion;
    void write() throws OIExcepcion;
    void delete() throws OIExcepcion;
    void close() throws OIExcepcion;
}