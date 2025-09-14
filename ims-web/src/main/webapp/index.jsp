<!DOCTYPE HTML>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>IMS Web</title>
    <link rel="stylesheet" href="assets/style/main.css" />
</head>
<body>
<div id="header">
    <h1>IMS Web: Sign In</h1>
</div>
<div id="body">
    <% if(session.getAttribute("user") != null) response.sendRedirect("dashboard.jsp");
        if(request.getParameter("error") != null && request.getParameter("message") != null) { %>
        <p id="error"><strong>Error <%= request.getParameter("error") %></strong>: <%= request.getParameter("message") %></p>
    <% } %>
    <h2>Sign In</h2>
    <p>Welcome to IMS Web! Please sign in to your IMS account to continue.</p>
    <form action="" method="post">
        <label for="username">Username:</label><br/>
        <input type="text" id="username" name="username" placeholder="Username" required/><br/><br/>
        <label for="password">Password:</label><br/>
        <input type="password" id="password" name="password" placeholder="Password" required/><br/><br/>
        <input type="submit" name="ims-signin-submit" value="Sign In" />
    </form>
</div>

<hr/>

</body>
</html>