package com.noeguepin.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String resource; // Nombre del recurso 
    private final String field;    // Campo por el que buscas
    private final String value;    // Valor buscado

	public ResourceAlreadyExistsException(String resource, String field, String value) {
		super("%s already exists with %s = %s".formatted(resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
	}

    public String getResource() { return resource; }
    public String getField() { return field; }
    public String getValue() { return value; }
	
}
