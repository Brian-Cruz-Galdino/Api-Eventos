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
import java.util.List;
import java.util.stream.Collectors;

@Path("/artistas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Artistas", description = "Operações relacionadas a artistas")
public class ArtistaResource {

    @Context
    UriInfo uriInfo;

    private ArtistaRepresentation rep(Artista a) {
        return ArtistaRepresentation.from(a, uriInfo);
    }

    private List<ArtistaRepresentation> repList(List<Artista> artistas) {
        return artistas.stream().map(this::rep).collect(Collectors.toList());
    }

    @GET
    @Operation(summary = "Listar todos os artistas")
    public Response getAll() {
        return Response.ok(repList(Artista.listAll())).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Buscar artista por ID")
    public Response getById(@PathParam("id") long id) {
        Artista entity = Artista.findById(id);
        if (entity == null)
            return Response.status(404).build();
        return Response.ok(rep(entity)).build();
    }
    @POST
    @Operation(summary = "Criar novo artista")
    @Transactional
    public Response insert(@Valid Artista artista) {
        artista.persist();
        return Response.created(URI.create("/artistas/" + artista.id)).entity(rep(artista)).build();
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Atualizar artista")
    @Transactional
    public Response update(@PathParam("id") long id, @Valid Artista newArtista) {
        Artista entity = Artista.findById(id);
        if (entity == null)
            return Response.status(404).build();

        entity.nome = newArtista.nome;
        entity.generoMusical = newArtista.generoMusical;
        entity.biografia = newArtista.biografia;

        return Response.ok(rep(entity)).build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Excluir artista")
    @Transactional
    public Response delete(@PathParam("id") long id) {
        Artista entity = Artista.findById(id);
        if (entity == null)
            return Response.status(404).build();

        Artista.deleteById(id);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/eventos")
    @Operation(summary = "Listar eventos do artista")
    public Response getEventosByArtista(@PathParam("id") long id) {
        Artista artista = Artista.findById(id);
        if (artista == null) {
            return Response.status(404).build();
        }

        List<EventoRepresentation> representations = artista.eventos.stream()
                .map(evento -> EventoRepresentation.from(evento, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(representations).build();
    }
}