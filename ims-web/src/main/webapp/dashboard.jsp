<%@ page import="com.johnnyconsole.ims.persistence.User" %>
<!DOCTYPE HTML>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SenVote Web</title>
    <link rel="stylesheet" href="assets/style/main.css" />
</head>
<body>
<div id="header">
    <h1>IMS Web: Dashboard</h1>
</div>
<div id="body">
    <% if(session.getAttribute("user") == null) response.sendRedirect("index.jsp?error=401 (Unauthorized)&message=You must be signed in to access this page.");
        User user = (User) session.getAttribute("user");
         if(request.getParameter("error") != null && request.getParameter("message") != null) { %>
            <p id="error"><strong>Error <%= request.getParameter("error") %></strong>: <%= request.getParameter("message") %></p>
        <% } %>
    <% String name = user.name.contains(" ") ? user.name.substring(0, user.name.indexOf(" ")) : user.name; %>
    <h2>Welcome to IMS, <%= name %>! &emsp;&emsp;&emsp; <a href="SignOutServlet" style="display: inline-block;">Sign Out</a></h2>
</div>

<hr/>

</body>
</html>