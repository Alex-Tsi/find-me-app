<#include "../security/user.ftlh">
<meta charset="UTF-8">
<title>Find me</title>
<link rel="stylesheet" href="/static/style.css">

<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Bootstrap CSS -->
<link  href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6"
      crossorigin="anonymous">
<#if (currentUserId < 0)>
    <script type="text/javascript">let currentUser = -1</script>
<#else>
    <script type="text/javascript">let currentUser = ${currentUserId}</script>
</#if>
<script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/webjars/sockjs-client/sockjs.min.js"></script>
<script type="text/javascript" src="/webjars/stomp-websocket/stomp.min.js"></script>
<script type="text/javascript" src="/static/js/chat.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
        crossorigin="anonymous"></script>

