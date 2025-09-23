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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/eventos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Eventos", description = "Operações relacionadas a eventos")
public class EventoResource {

    @Context
    UriInfo uriInfo;

    private EventoRepresentation rep(Evento e) {
        return EventoRepresentation.from(e, uriInfo);
    }

    private List<EventoRepresentation> repList(List<Evento> eventos) {
        return eventos.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(summary = "Listar todos os eventos")
    public Response getAll() {
        return Response.ok(repList(Evento.listAll())).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Buscar evento por ID")
    public Response getById(@PathParam("id") long id) {
        Evento entity = Evento.findById(id);
        if (entity == null)
            return Response.status(404).build();
        return Response.ok(rep(entity)).build();
    }

    @POST
    @Operation(summary = "Criar novo evento")
    @Transactional
    public Response insert(@Valid Evento evento) {
        // Se houver artistas enviados, carregar do banco
        if (evento.artistas != null && !evento.artistas.isEmpty()) {
            List<Artista> artistasPersistidos = new ArrayList<>();
            for (Artista artista : evento.artistas) {
                if (artista.id != null) {
                    Artista artistaPersistido = Artista.findById(artista.id);
                    if (artistaPersistido != null) {
                        artistasPersistidos.add(artistaPersistido);
                    }
                }
            }
            evento.artistas = artistasPersistidos;
        } else {
            evento.artistas = new ArrayList<>();
        }

        evento.persist();
        return Response.created(URI.create("/eventos/" + evento.id)).entity(rep(evento)).build();
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Atualizar evento")
    @Transactional
    public Response update(@PathParam("id") long id, @Valid Evento newEvento) {
        Evento entity = Evento.findById(id);
        if (entity == null)
            return Response.status(404).build();

        entity.nome = newEvento.nome;
        entity.descricao = newEvento.descricao;
        entity.dataEvento = newEvento.dataEvento;
        entity.local = newEvento.local;
        entity.capacidadeMaxima = newEvento.capacidadeMaxima;
        entity.precoIngresso = newEvento.precoIngresso;
        entity.status = newEvento.status;

        // LÓGICA DE ATUALIZAÇÃO DE ARTISTAS CORRIGIDA
        if (newEvento.artistas != null) {
            // 1. Limpa a lista de artistas atuais do evento.
            // É importante iterar e remover dos dois lados para manter o estado do Hibernate consistente.
            for (Artista artista : new ArrayList<>(entity.artistas)) {
                artista.eventos.remove(entity);
            }
            entity.artistas.clear();

            // 2. Adiciona os novos artistas da requisição
            for (Artista artistaInfo : newEvento.artistas) {
                if (artistaInfo.id != null) {
                    Artista artistaPersistido = Artista.findById(artistaInfo.id);
                    if (artistaPersistido != null) {
                        // Adiciona o artista ao evento e o evento ao artista
                        entity.artistas.add(artistaPersistido);
                        artistaPersistido.eventos.add(entity);
                    }
                }
            }
        }

        // A persistência é gerenciada pela anotação @Transactional
        return Response.ok(rep(entity)).build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Excluir evento")
    @Transactional
    public Response delete(@PathParam("id") long id) {
        Evento entity = Evento.findById(id);
        if (entity == null)
            return Response.status(404).build();

        Evento.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("{id}/artistas")
    @Operation(summary = "Adicionar artistas ao evento")
    @Transactional
    public Response addArtistas(@PathParam("id") long id, List<Long> artistaIds) {
        Evento evento = Evento.findById(id);
        if (evento == null) {
            return Response.status(404).build();
        }

        for (Long artistaId : artistaIds) {
            Artista artista = Artista.findById(artistaId);
            if (artista != null && !evento.artistas.contains(artista)) {
                evento.artistas.add(artista);
                artista.eventos.add(evento);
            }
        }

        return Response.ok(rep(evento)).build();
    }

    @DELETE
    @Path("{id}/artistas/{artistaId}")
    @Operation(summary = "Remover artista do evento")
    @Transactional
    public Response removeArtista(@PathParam("id") long id, @PathParam("artistaId") long artistaId) {
        Evento evento = Evento.findById(id);
        Artista artista = Artista.findById(artistaId);

        if (evento == null || artista == null) {
            return Response.status(404).build();
        }

        evento.artistas.remove(artista);
        artista.eventos.remove(evento);

        return Response.ok(rep(evento)).build();
    }

    @GET
    @Path("{id}/artistas")
    @Operation(summary = "Listar artistas do evento")
    public Response getArtistasByEvento(@PathParam("id") long id) {
        Evento evento = Evento.findById(id);
        if (evento == null) {
            return Response.status(404).build();
        }

        List<ArtistaRepresentation> representations = evento.artistas.stream()
                .map(artista -> ArtistaRepresentation.from(artista, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(representations).build();
    }

    @GET
    @Path("{id}/ingressos")
    @Operation(summary = "Listar ingressos do evento")
    public Response getIngressosByEvento(@PathParam("id") long id) {
        List<Ingresso> ingressos = Ingresso.find("evento.id", id).list();
        if (ingressos.isEmpty()) {
            return Response.status(204).build();
        }

        List<IngressoRepresentation> representations = ingressos.stream()
                .map(ingresso -> IngressoRepresentation.from(ingresso, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(representations).build();
    }

    @PUT
    @Path("{id}/status")
    @Operation(summary = "Atualizar status do evento")
    @Transactional
    public Response updateStatus(
            @PathParam("id") long id,
            @QueryParam("status") Evento.StatusEvento novoStatus) {

        Evento evento = Evento.findById(id);
        if (evento == null) {
            return Response.status(404).build();
        }

        evento.status = novoStatus;
        return Response.ok(rep(evento)).build();
    }
}