package com.johnnyconsole.ims.restful;

import com.johnnyconsole.ims.persistence.User;
import com.johnnyconsole.ims.persistence.interfaces.UserDaoLocal;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Path("/user")
@RequestScoped
public class UserAPI {

    @EJB private UserDaoLocal userDao;

    @POST
    @Path("/signin")
    @Produces(APPLICATION_JSON)
    public Response signin(
            @NotNull @NotEmpty @FormParam("username") String username,
            @NotNull @NotEmpty @FormParam("password") String password) {
        User user = userDao.getUser(username);
        String response = "{\n";
        if (user == null) {
            response += "\t\"status\": 404\n";
            response += "\t\"message\": \"User '" + username + "' not found\"\n";
        }
        else if(user.verifyPassword(password)) {
            response += "\t\"status\": 200\n";
            response += "\t\"user\": {\n";
            response += "\t\t\"username\": \"" + username + "\"\n";
            response += "\t\t\"name\": \"" + user.name + "\"\n";
            response += "\t\t\"access\": " + user.accessLevel + "\n";
            response += "\t}";
        }
        else {
            response += "\t\"status\": 401\n";
            response += "\t\"message\": \"Incorrect password for user '" + username + "'\"\n";
        }

        return Response.ok(response + "\n}").build();
    }
}
