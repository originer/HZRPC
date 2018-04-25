<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org" >
<head>
    <meta content="text/html;charset=UTF-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <link th:href="@{/bootstrap/css/bootstrap.min.css}" rel="stylesheet"/>
    <link th:href="@{/bootstrap/css/bootstrap-theme.min.css}" rel="stylesheet"/>
</head>
<body>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">访问model</h3>
    </div>
    <div class="panel-body">
        <span th:text="${serviceModelList}"></span>
    </div>
</div>
<div th:if="${not} #lists.isEmpty(serviceModelList)}">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">列表</h3>
        </div>
        <div class="panel-body">
            <ul class="list-group">
                <li class="list-group-item" th:each="serviceModel : ${serviceModelList}">
                    <span th:text="${serviceModel.serviceName}"></span>
                    <button class="btn" th:onlick="'getName(\''+${serviceModel.name}+'\');'">获得名字</button>
                </li>
            </ul>
        </div>
    </div>
</div>
<script th:src="@{/jquery/jquery-3.2.1.min.js}"></script>
<script th:src="@{/bootstrap/js/bootstrap.min.js}"></script>
<script th:inline="javascript">
    var single = [[${singlePerson}]];
    console.log(single.name+"/"+single.age);
    function getName(name) {
        console.log(name);
    }
</script>
</body>
</html>