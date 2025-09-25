package com.johnnyconsole.ims.restful;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.johnnyconsole.ims.persistence.User;
import com.johnnyconsole.ims.persistence.interfaces.UserDaoLocal;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.util.List;

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
            response += "\t\"status\": 404,\n";
            response += "\t\"category\": \"Not Found\",\n";
            response += "\t\"message\": \"User '" + username + "' not found\"\n";
        }
        else if(user.verifyPassword(password)) {
            response += "\t\"status\": 200,\n";
            response += "\t\"user\": {\n";
            response += "\t\t\"username\": \"" + username + "\",\n";
            response += "\t\t\"name\": \"" + user.name + "\",\n";
            response += "\t\t\"access\": " + user.accessLevel + "\n";
            response += "\t}";
        }
        else {
            response += "\t\"status\": 401,\n";
            response += "\t\"category\": \"Unauthorized\",\n";
            response += "\t\"message\": \"Incorrect password for user '" + username + "'\"\n";
        }

        return Response.ok(response + "\n}").build();
    }

    @POST
    @Path("/add")
    @Produces(APPLICATION_JSON)
    public Response add(@NotEmpty @FormParam("username") String username,
                        @NotEmpty @FormParam("name") String name,
                        @NotEmpty @FormParam("password") String password,
                        @NotEmpty @FormParam("confirm") String confirm,
                        @FormParam("access") int access,
                        @NotEmpty @FormParam("admin_username") String adminUsername) {

        String response = "{\n";
        try {
            if (userDao.userExists(adminUsername)) {
                User admin = userDao.getUser(adminUsername);
                if (admin.accessLevel == 1) {
                    if (!userDao.userExists(username)) {
                        if (password.equals(confirm)) {
                            User user = new User(username, name,
                                    BCrypt.with(BCrypt.Version.VERSION_2A)
                                            .hashToString(12, password.toCharArray()), access);
                            userDao.addUser(user);
                            response += "\t\"status\": 200,\n";
                            response += "\t\"message\": \"User '" + username + "' added successfully\"\n";
                        } else {
                            response += "\t\"status\": 409,\n";
                            response += "\t\"category\": \"Conflict\",\n";
                            response += "\t\"message\": \"Passwords do not match\"\n";
                        }
                    } else {
                        response += "\t\"status\": 409,\n";
                        response += "\t\"category\": \"Conflict\",\n";
                        response += "\t\"message\": \"An existing record was found for user '" + username + "'\"\n";
                    }
                } else {
                    response += "\t\"status\": 401,\n";
                    response += "\t\"category\": \"Unauthorized\",\n";
                    response += "\t\"message\": \"User '" + adminUsername + "' is not an Administrator\"\n";
                }
            } else {
                response += "\t\"status\": 404,\n";
                response += "\t\"category\": \"Not Found\",\n";
                response += "\t\"message\": \"No record found for '" + adminUsername + "'\"\n";
            }
        } catch(Exception e) {
            response += "\t\"status\": 400,\n";
            response += "\t\"category\": \"Bad Request\",\n";
            response += "\t\"message\": \"Missing or empty parameter\n";
        }
        return Response.ok(response + "}").build();
    }

    @GET
    @Path("/all-except-{except}")
    @Produces(APPLICATION_JSON)
    public Response all_except(@NotEmpty @PathParam("except") String except) {
        String response = "{\n\t\"users\": [\n";

        List<User> users = userDao.getUsersExcept(except);
        for (int i = 0; i < users.size(); i++) {
            if(i < users.size() - 1) response += "\t\t\"" + users.get(i).name + " (" + users.get(i).username + ")\",\n";
            else response += "\t\t\"" + users.get(i).name + " (" + users.get(i).username + ")\"";
        }
        return Response.ok(response + "\n\t]\n}").build();
    }

    @POST
    @Path("/delete")
    @Produces(APPLICATION_JSON)
    public Response delete(@NotEmpty @FormParam("username") String username,
                           @NotEmpty @FormParam("admin_username") String adminUsername) {

        String response = "{\n";
        try {
            if (userDao.userExists(adminUsername)) {
                User admin = userDao.getUser(adminUsername);
                if (admin.accessLevel == 1) {
                    if (userDao.userExists(username)) {
                        userDao.removeUser(userDao.getUser(username), adminUsername);
                        response += "\t\"status\": 200,\n";
                        response += "\t\"message\": \"User '" + username + "' deleted successfully\"\n";
                    } else {
                        response += "\t\"status\": 404,\n";
                        response += "\t\"category\": \"Not Found\",\n";
                        response += "\t\"message\": \"No record found for '" + username + "'\"\n";
                    }
                } else {
                    response += "\t\"status\": 401,\n";
                    response += "\t\"category\": \"Unauthorized\",\n";
                    response += "\t\"message\": \"User '" + adminUsername + "' is not an Administrator\"\n";
                }
            } else {
                response += "\t\"status\": 404,\n";
                response += "\t\"category\": \"Not Found\",\n";
                response += "\t\"message\": \"No  record found for '" + adminUsername + "'\"\n";
            }
        } catch(Exception e) {
            response += "\t\"status\": 400,\n";
            response += "\t\"category\": \"Bad Request\",\n";
            response += "\t\"message\": \"Missing or empty parameter\n";
        }
        return Response.ok(response + "}").build();
    }
}
