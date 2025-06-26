package oop1.section10;

import java.io.*;
import java.net.*;

public class SimpleHTTPServer {
  private static final int PORT = 8088;

  private static String getIndexHtml() {
    return """
        <!DOCTYPE html>
        <html lang="ja">
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <meta charset="UTF-8">
          <title>SimpleHTTPServer</title>
          <link rel="stylesheet" href="style.css">
        </head>
        <body>
          <main>
            <h1>このページはSimpleHTTPServerより生成されて返されています。</h1>
            <p><button class="fire">Push!!</button></p>
            <p class="copyright">K24132 - 町田 渉</p>
          </main>
          <script src="script.js"></script>
        </body>
        </html>""";
  }

  private static String getHelloHtml(String name) {
    return """
        <!DOCTYPE html>
        <html lang="ja">
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <meta charset="UTF-8">
          <title>SimpleHTTPServer</title>
          <link rel="stylesheet" href="style.css">
        </head>
        <body>
          <main>
            <h1>こんにちは！""" + name + """
        さん！！</h1>
          </main>
        </body>
        </html>
        """;
  }

  private static String getStyleCss() {
    return """
        * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
        }
        body {
          height: 100vh;
          display: flex;
          justify-content: center;
          align-items: center;
        }
        main {
          height: 450px;
          max-height: 90vh;
          width: 800px;
          max-width: 90vw;
          border-radius: 10px;
          box-shadow: rgba(0, 0, 0, 0.1) 0px 20px 60px -10px;
          display: flex;
          justify-content: center;
          align-items: center;
          flex-direction: column;
        }
        h1 {
          padding: 0 3em;
          margin-bottom: 2em;
          text-align: center;
        }
        button {
          font-size: 1.25em;
          padding: 0.5em 1em;
        }
        .copyright {
          margin-top: 20px;
          text-decoration: underline;
          font-style: italic;
        }
        """;
  }

  private static String getScriptJs() {
    return """
        var btn = document.querySelector('button.fire');
        btn.addEventListener('click', function() {
          alert('Hello, SimpleHTTPServer!!');
        });
        """;
  }

  private static String getNotFoundHtml() {
    return """
        <!DOCTYPE html>
        <html lang="ja">
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1">
          <meta charset="UTF-8">
          <title>404</title>
          <link rel="stylesheet" href="style.css">
        </head>
        <body>
          <main>
            <h1>404... Not Found!</h1>
          </main>
        </body>
        </html>
        """;
  }

  private static void handleRequest(Socket clientSocket) throws IOException {
    try (var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        var out = new PrintWriter(clientSocket.getOutputStream(), true)) {

      var requestLine = in.readLine();
      if (requestLine == null)
        return;

      // NOTE: HTTPリクエストヘッダーを読み飛ばす
      String line;
      while ((line = in.readLine()) != null && !line.isEmpty()) {
      }

      // リクエストラインを解析
      var parts = requestLine.split(" ");
      if (parts.length < 2)
        return;

      var path = parts[1];

      // パスからクエリパラメータを分離
      var queryString = "";
      var questionIndex = path.indexOf('?');
      if (questionIndex != -1) {
        queryString = path.substring(questionIndex + 1);
        path = path.substring(0, questionIndex);
      }

      var response = generateResponse(path, queryString);
      out.print(response);
      out.flush();
    }
  }

  private static String generateResponse(String path, String queryString) {
    record ResponseData(String statusLine, String contentType, String body) {
    }

    var responseData = switch (path) {
      case "/", "/index.html" -> new ResponseData(
          "HTTP/1.0 200 OK",
          "text/html; charset=utf-8",
          getIndexHtml());

      case "/style.css" -> new ResponseData(
          "HTTP/1.0 200 OK",
          "text/css; charset=utf-8",
          getStyleCss());

      case "/script.js" -> new ResponseData(
          "HTTP/1.0 200 OK",
          "text/javascript; charset=utf-8",
          getScriptJs());

      case "/hello" -> new ResponseData(
          "HTTP/1.0 200 OK",
          "text/html; charset=utf-8",
          getHelloHtml(extractNameFromQuery(queryString)));

      default -> new ResponseData(
          "HTTP/1.0 404 Not Found",
          "text/html; charset=utf-8",
          getNotFoundHtml());
    };

    return responseData.statusLine() + "\r\n" +
        "Content-Type: " + responseData.contentType() + "\r\n" +
        "\r\n" +
        responseData.body();
  }

  private static String extractNameFromQuery(String queryString) {
    if (queryString.startsWith("name=")) {
      try {
        return URLDecoder.decode(queryString.substring(5), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        return "Unknown";
      }
    }
    return "Unknown";
  }

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("SimpleHTTPServer started on port " + PORT);
      System.out.println("Access: http://localhost:" + PORT);

      while (true) {
        try (Socket clientSocket = serverSocket.accept()) {
          handleRequest(clientSocket);
        } catch (IOException e) {
          System.err.println("Error handling client request: " + e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }
}
