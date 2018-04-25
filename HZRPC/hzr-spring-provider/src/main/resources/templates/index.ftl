<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Theme Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="/static/css/bootstrap.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/static/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="/static/css/theme.css" rel="stylesheet">
</head>

<body>

<!-- Fixed navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Bootstrap theme</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="/test/list">List</a></li>
                <li><a href="/test/index">Test</a></li>
                <li><a href="#contact">Contact</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false">Dropdown <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li role="separator" class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="container theme-showcase" role="main">

    <div class="page-header">
        <div id="board" class="jumbotron">
            <h4><span id="status1" class="text-center">调用结果：</span> <span id="status" class="label label-success"></span><br></h4>
            <h4><span id="result1" class="text-center">返回结果：</span> <span id="result" class="label label-success"></span><br></h4>
            <h4><span id="time1" class="text-center">过程耗时：</span> <span id="time" class="label label-success"></span><br></h4>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title"></h2>
        </div>
        <div class="panel-body">
            <li class="list-group-item" th:each="service : ${serviceList}">
                <h3>
                <span class="label label-info" th:text="${service.serviceName}">服务名称</span>
                <span class="label label-success" th:text="${service.serviceProvider.ip}">服务IP</span>
                <span class="label label-success" th:text="${service.serviceProvider.port}">服务IP</span>
                    <button type="button" class="btn btn btn-default" th:onclick="'javascript:send(\''+${service.serviceName}+'\');'">调用服务</button>
                </h3>
            </li>

        </div>

    </div>
</div> <!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/static/js/jquery.js"></script>
<script src="/static/js/docs.min.js"></script>
<script src="/static/js/bootstrap.js"></script>
<script th:inline="javascript" type="text/javascript">
    function send(s) {
        var dataMap
//        alert(s);
        dataMap = "serviceName=" + s + "&funcName=say";
        $.get("/test/callService?" + dataMap, function (data) {
            $("#result").text(" "+data.result);
            $("#status").text(data.status);
            $("#time").text(data.consumTime+"ms");
        })
    }
</script>
</body>
</html>
