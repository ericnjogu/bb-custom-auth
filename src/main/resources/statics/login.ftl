<html>
<head>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
    rel="stylesheet"
    integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB"
    crossorigin="anonymous">
</head>
<body class="text-center container">
<p>
    <#if ERROR_MESSAGE??>
    <span style="color: red;">
        Your login attempt was unsuccessful, try again
       <br/><br>
        Reason: ${ERROR_MESSAGE}
    </span>
    </#if>
</p>
<form class="form-signin" action="${LOGIN_PAGE_URL}" method="post">
    <img class="w-25" src="https://www.fintechfutures.com/files/2016/08/backbase.png">
    <div class="col-12 mb-4">
        <label for="inputUsername" class="sr-only">Username</label>
        <input type="text" id="inputUsername" class="form-control" placeholder="Username"
        name="${USERNAME_FIELD}" required="required" autofocus="autofocus">
    </div>
    <div class="col-12 mb-4">
        <label for="email" class="sr-only">Email</label>
        <input type="email" id="email" class="form-control" placeholder="email"
               name="${EMAIL_FIELD}" required="required" autofocus="autofocus">
    </div>
    <div class="col-12 mb-4">
        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" class="form-control" placeholder="password"
               name="${PASSWORD_FIELD}" required="required" autofocus="autofocus">
    </div>
    <button class="btn bln-lg btn-primary btn-block" type="submit">Sign in</button>
</form>
</body>
</html>