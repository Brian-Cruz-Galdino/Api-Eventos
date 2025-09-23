package org.acme;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/ingressos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ingressos", description = "Operações relacionadas a ingressos")
public class IngressoResource {

    @Context
    UriInfo uriInfo;

    private IngressoRepresentation rep(Ingresso i) {
        return IngressoRepresentation.from(i, uriInfo);
    }

    private List<IngressoRepresentation> repList(List<Ingresso> ingressos) {
        return ingressos.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(summary = "Listar todos os ingressos")
    public Response getAll() {
        List<Ingresso> ingressos = Ingresso.listAll();
        return Response.ok(repList(ingressos)).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Buscar ingresso por ID")
    public Response getById(@PathParam("id") long id) {
        Ingresso entity = Ingresso.findById(id);
        if (entity == null)
            return Response.status(404).build();
        return Response.ok(rep(entity)).build();
    }

    @POST
    @Operation(summary = "Criar novo ingresso")
    @Transactional
    public Response insert(@Valid CreateIngressoRequest request) { // 1. Mude o parâmetro para o DTO
        // 2. Busque o evento pelo ID fornecido no request
        Evento evento = Evento.findById(request.getEventoId());
        if (evento == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Evento não encontrado").build();
        }

        // 3. Verifique a capacidade do evento
        long ingressosVendidos = Ingresso.count("evento.id", evento.id);
        if (ingressosVendidos + request.getQuantidade() > evento.capacidadeMaxima) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Capacidade máxima do evento excedida").build();
        }

        // 4. Crie uma nova entidade Ingresso a partir do request
        Ingresso ingresso = new Ingresso();
        ingresso.nomeComprador = request.getNomeComprador();
        ingresso.emailComprador = request.getEmailComprador();
        ingresso.quantidade = request.getQuantidade();
        ingresso.evento = evento; // Associa o evento persistido

        // 5. Defina os valores controlados pelo servidor
        ingresso.dataCompra = LocalDateTime.now();
        ingresso.precoTotal = request.getQuantidade() * evento.precoIngresso;
        ingresso.status = Ingresso.StatusIngresso.RESERVADO;

        ingresso.persist();

        // Atualizar status do evento se necessário
        if (Ingresso.count("evento.id", evento.id) >= evento.capacidadeMaxima) {
            evento.status = Evento.StatusEvento.ESGOTADO;
        }

        return Response.created(URI.create("/ingressos/" + ingresso.id)).entity(rep(ingresso)).build();
    }
    @PUT
    @Path("{id}")
    @Operation(summary = "Atualizar ingresso")
    @Transactional
    public Response update(@PathParam("id") long id, @Valid Ingresso newIngresso) {
        Ingresso entity = Ingresso.findById(id);
        if (entity == null)
            return Response.status(404).build();

        entity.nomeComprador = newIngresso.nomeComprador;
        entity.emailComprador = newIngresso.emailComprador;
        entity.quantidade = newIngresso.quantidade;
        entity.status = newIngresso.status;

        return Response.ok(rep(entity)).build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Excluir ingresso")
    @Transactional
    public Response delete(@PathParam("id") long id) {
        Ingresso entity = Ingresso.findById(id);
        if (entity == null)
            return Response.status(404).build();

        Ingresso.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("{id}/status")
    @Operation(summary = "Atualizar status do ingresso")
    @Transactional
    public Response updateStatus(
            @PathParam("id") long id,
            @QueryParam("status") Ingresso.StatusIngresso novoStatus) {

        Ingresso ingresso = Ingresso.findById(id);
        if (ingresso == null) {
            return Response.status(404).build();
        }

        ingresso.status = novoStatus;
        return Response.ok(rep(ingresso)).build();
    }
}