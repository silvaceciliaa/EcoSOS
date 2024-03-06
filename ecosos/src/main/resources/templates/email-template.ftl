<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>EcoSOS</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: 'Circular', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(to right, #088220 50%, #ffffff 50%);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            transition: background 0.5s ease-in-out;
        }

        .container {
            width: 600px;
            background-color: #ffffff;
            border-radius: 10px;
            overflow: hidden;
        }

        .header {
            padding: 20px;
            text-align: center;
            color: #000000;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
        }

        .content {
            padding: 20px;
            color: #333333;
        }

        .footer {
            background-color: #088220;
            padding: 10px;
            text-align: center;
            color: #ffffff;
            border-bottom-left-radius: 10px;
            border-bottom-right-radius: 10px;
        }

        a {
            color: #088220;
            text-decoration: none;
        }

        body:hover {
            background: linear-gradient(to right, #ffffff 50%, #088220 50%);
        }
    </style>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Circular:wght@300&display=swap">
</head>

<body>
    <div class="container">
        <div class="header">
            <h1 style="font-family: 'Circular', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #000000;">EcoSOS</h1>
        </div>
        <div class="content">
            <div style="font-size: 14px;">
                ${mensagem}
            </div>
            <div style="font-size: 12px; color: #333333;">
                <p>Qualquer dúvida é só contatar o suporte pelo e-mail ecossossuporte@outlook.com</p>
                <br>
                <a href="mailto:${email}">${email}</a>

            </div>
            <div>
                <p style="color: #333333;">Att, Sistema.</p>
            </div>
        </div>
        <div class="footer">
            © 2024 EcoSOS. Todos os direitos reservados.
        </div>
    </div>
</body>

</html>
