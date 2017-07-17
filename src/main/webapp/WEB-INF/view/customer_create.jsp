<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <title>客户管理，创建客户</title>
</head>
<body>
<h1>创建客户</h1>
<form id="customer_form" enctype="multipart/form-data">
    <table>
        <tr>
            <th>客户名称：</th>
            <td>
                <input type="text" name="name" value="${customer.name}">
            </td>
        </tr>
        <tr>
            <th>联系人：</th>
            <td>
                <input type="text" name="contact" value="${customer.contact}">
            </td>
        </tr>
        <tr>
            <th>电话号码：</th>
            <td>
                <input type="text" name="telephone" value="${customer.telephone}">
            </td>
        </tr>
        <tr>
            <th>邮箱地址：</th>
            <td>
                <input type="text" name="email" value="${customer.email}">
            </td>
        </tr>
        <tr>
            <th>照片：</th>
            <td>
                <input type="file" name="phone" value="${customer.phone}">
            </td>
        </tr>

    </table>
    <button type="submit">保存</button>
</form>

<script src="${BASE}/asset/lib/jquery/jquery.min.js"></script>
<script src="${BASE}/asset/lib/jquery-form/jquery.form.min.js"></script>
<script>
    当表单提交后，请求后转发到CustomerController的createSubmit方法上
    $(function(){
        $('#customer_form').ajaxForm({
            type:'post',
            url:'${BASE}/customer_create',
            success:function (data) {
                if(data){
                    location.href = '${BASE}/customer';
                }
            }
        })
    })
</script>
</body>
</html>
