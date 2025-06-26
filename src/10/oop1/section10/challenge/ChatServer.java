package oop1.section10.challenge;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

record User(String username, Socket socket, PrintWriter writer) {
  public void sendMessage(String message) {
    writer.println(message);
    writer.flush();
  }
}

record Group(String name, Set<String> members) {
  public static Group create(String name) {
    return new Group(name, ConcurrentHashMap.newKeySet());
  }

  public Group addMember(String username) {
    var newMembers = new HashSet<>(members);
    newMembers.add(username);
    return new Group(name, newMembers);
  }

  public Group removeMember(String username) {
    var newMembers = new HashSet<>(members);
    newMembers.remove(username);
    return new Group(name, newMembers);
  }
}

record Command(String type, List<String> args) {
  public static Command parse(String line) {
    var parts = line.trim().split("\\s+", 2);
    if (parts.length == 1) {
      return new Command(parts[0], List.of());
    }

    var command = parts[0];
    var remaining = parts[1];

    return switch (command) {
      case "REGISTER", "JOIN", "LEAVE", "LIST_GROUP" ->
        new Command(command, List.of(remaining));
      case "LIST_USERS" ->
        new Command(command, List.of());
      case "BROADCAST" ->
        parseMessageCommand(command, remaining);
      case "GROUPCAST" ->
        parseGroupMessage(command, remaining);
      default ->
        new Command(command, List.of());
    };
  }

  private static Command parseMessageCommand(String command, String remaining) {
    var spaceIndex = remaining.indexOf(' ');
    if (spaceIndex == -1)
      return new Command(command, List.of());

    try {
      var lengthStr = remaining.substring(0, spaceIndex);
      var length = Integer.parseInt(lengthStr);
      var messageStart = spaceIndex + 1;

      if (messageStart < remaining.length()) {
        var fullMessage = remaining.substring(messageStart);
        var message = fullMessage.length() >= length ? fullMessage.substring(0, length) : fullMessage;
        return new Command(command, List.of(String.valueOf(length), message));
      }
    } catch (NumberFormatException e) {

    }
    return new Command(command, List.of());
  }

  private static Command parseGroupMessage(String command, String remaining) {

    var firstSpace = remaining.indexOf(' ');
    if (firstSpace == -1)
      return new Command(command, List.of());

    var groupName = remaining.substring(0, firstSpace);
    var afterGroup = remaining.substring(firstSpace + 1);

    var secondSpace = afterGroup.indexOf(' ');
    if (secondSpace == -1)
      return new Command(command, List.of());

    try {
      var lengthStr = afterGroup.substring(0, secondSpace);
      var length = Integer.parseInt(lengthStr);
      var messageStart = secondSpace + 1;

      if (messageStart < afterGroup.length()) {
        var fullMessage = afterGroup.substring(messageStart);
        var message = fullMessage.length() >= length ? fullMessage.substring(0, length) : fullMessage;
        return new Command(command, List.of(groupName, String.valueOf(length), message));
      }
    } catch (NumberFormatException e) {

    }
    return new Command(command, List.of());
  }
}

record Response(int statusCode, String message) {
  public static final Response CREATED_USER = new Response(201, "CREATED");
  public static final Response OK = new Response(200, "OK");
  public static final Response CONFLICT_USERNAME = new Response(409, "CONFLICT Username already taken");
  public static final Response BAD_REQUEST_USERNAME = new Response(400, "BAD_REQUEST Invalid username format");
  public static final Response UNAUTHORIZED = new Response(401, "UNAUTHORIZED User not registered");
  public static final Response FORBIDDEN_GROUP = new Response(403, "FORBIDDEN Not a member of group");
  public static final Response NOT_FOUND_GROUP = new Response(404, "NOT_FOUND Not in group");
  public static final Response CONFLICT_GROUP = new Response(409, "CONFLICT Already in group");

  public String format() {
    return statusCode + " " + message;
  }

  public String formatWithData(String data) {
    return statusCode + " " + message + " " + data;
  }
}

enum ChatServerError {
  INVALID_USERNAME,
  USERNAME_TAKEN,
  USER_NOT_REGISTERED,
  NOT_IN_GROUP,
  ALREADY_IN_GROUP,
  INVALID_COMMAND
}

record CommandResult<T>(T value, ChatServerError error) {
  public static <T> CommandResult<T> success(T value) {
    return new CommandResult<>(value, null);
  }

  public static <T> CommandResult<T> error(ChatServerError error) {
    return new CommandResult<>(null, error);
  }

  public boolean isError() {
    return error != null;
  }
}

public class ChatServer {
  private static final int PORT = 12345;

  private final Map<String, User> users = new ConcurrentHashMap<>();
  private final Map<String, Group> groups = new ConcurrentHashMap<>();
  private final Map<String, Set<String>> userGroups = new ConcurrentHashMap<>();

  private CommandResult<String> registerUser(String username, Socket socket, PrintWriter writer) {
    if (!isValidUsername(username)) {
      return CommandResult.error(ChatServerError.INVALID_USERNAME);
    }

    if (users.containsKey(username)) {
      return CommandResult.error(ChatServerError.USERNAME_TAKEN);
    }

    users.put(username, new User(username, socket, writer));
    userGroups.put(username, ConcurrentHashMap.newKeySet());
    return CommandResult.success(username);
  }

  private boolean isValidUsername(String username) {
    return username != null &&
        username.length() <= 20 &&
        username.matches("[a-zA-Z0-9_]+");
  }

  private CommandResult<String> joinGroup(String username, String groupName) {
    if (!users.containsKey(username)) {
      return CommandResult.error(ChatServerError.USER_NOT_REGISTERED);
    }

    var userGroupSet = userGroups.get(username);
    if (userGroupSet.contains(groupName)) {
      return CommandResult.error(ChatServerError.ALREADY_IN_GROUP);
    }

    groups.computeIfAbsent(groupName, Group::create);
    groups.compute(groupName, (name, group) -> group.addMember(username));
    userGroupSet.add(groupName);

    notifyGroupJoin(groupName, username);
    return CommandResult.success(groupName);
  }

  private CommandResult<String> leaveGroup(String username, String groupName) {
    if (!users.containsKey(username)) {
      return CommandResult.error(ChatServerError.USER_NOT_REGISTERED);
    }

    var userGroupSet = userGroups.get(username);
    if (!userGroupSet.contains(groupName)) {
      return CommandResult.error(ChatServerError.NOT_IN_GROUP);
    }

    groups.computeIfPresent(groupName, (name, group) -> group.removeMember(username));
    userGroupSet.remove(groupName);

    notifyGroupLeave(groupName, username);
    return CommandResult.success(groupName);
  }

  private CommandResult<Void> broadcastMessage(String username, String message) {
    if (!users.containsKey(username)) {
      return CommandResult.error(ChatServerError.USER_NOT_REGISTERED);
    }

    return CommandResult.success(null);
  }

  private void sendBroadcastNotification(String username, String message) {
    var notification = String.format("NOTIFY BROADCAST %s %d %s", username, message.length(), message);
    users.values().forEach(user -> user.sendMessage(notification));
  }

  private CommandResult<Void> groupcastMessage(String username, String groupName, String message) {
    if (!users.containsKey(username)) {
      return CommandResult.error(ChatServerError.USER_NOT_REGISTERED);
    }

    var userGroupSet = userGroups.get(username);
    if (!userGroupSet.contains(groupName)) {
      return CommandResult.error(ChatServerError.NOT_IN_GROUP);
    }

    return CommandResult.success(null);
  }

  private void sendGroupcastNotification(String username, String groupName, String message) {
    var group = groups.get(groupName);
    if (group != null) {
      var notification = String.format("NOTIFY GROUPCAST %s %s %d %s", groupName, username, message.length(), message);
      group.members().forEach(member -> {
        var user = users.get(member);
        if (user != null) {
          user.sendMessage(notification);
        }
      });
    }
  }

  private void notifyGroupJoin(String groupName, String username) {
    var group = groups.get(groupName);
    if (group != null) {
      var notification = String.format("NOTIFY USER_JOINED %s %s", groupName, username);
      group.members().stream()
          .filter(member -> !member.equals(username))
          .forEach(member -> {
            var user = users.get(member);
            if (user != null) {
              user.sendMessage(notification);
            }
          });
    }
  }

  private void notifyGroupLeave(String groupName, String username) {
    var group = groups.get(groupName);
    if (group != null) {
      var notification = String.format("NOTIFY USER_LEFT %s %s", groupName, username);
      group.members().forEach(member -> {
        var user = users.get(member);
        if (user != null) {
          user.sendMessage(notification);
        }
      });
    }
  }

  private void handleClient(Socket clientSocket) {
    String currentUser = null;

    try (var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        var out = new PrintWriter(clientSocket.getOutputStream(), true)) {

      String line;
      while ((line = in.readLine()) != null) {
        var command = Command.parse(line);
        var response = processCommand(command, currentUser, clientSocket, out);

        if (command.type().equals("REGISTER") && response.startsWith("201")) {
          currentUser = command.args().get(0);
        }

        if (!response.isEmpty()) {
          out.println(response);
          out.flush();
        }

        sendNotificationsAfterCommand(command, currentUser);
      }
    } catch (IOException e) {
      System.err.println("Client handling error: " + e.getMessage());
    } finally {
      if (currentUser != null) {
        users.remove(currentUser);
        userGroups.remove(currentUser);
      }
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("Error closing client socket: " + e.getMessage());
      }
    }
  }

  private void sendNotificationsAfterCommand(Command command, String currentUser) {
    switch (command.type()) {
      case "BROADCAST" -> {
        if (command.args().size() >= 2 && currentUser != null) {
          sendBroadcastNotification(currentUser, command.args().get(1));
        }
      }
      case "GROUPCAST" -> {
        if (command.args().size() >= 3 && currentUser != null) {
          sendGroupcastNotification(currentUser, command.args().get(0), command.args().get(2));
        }
      }
    }
  }

  private String processCommand(Command command, String currentUser, Socket socket, PrintWriter writer) {
    return switch (command.type()) {
      case "REGISTER" -> {
        if (command.args().isEmpty()) {
          yield Response.BAD_REQUEST_USERNAME.format();
        }
        var result = registerUser(command.args().get(0), socket, writer);
        yield result.isError() ? switch (result.error()) {
          case INVALID_USERNAME -> Response.BAD_REQUEST_USERNAME.format();
          case USERNAME_TAKEN -> Response.CONFLICT_USERNAME.format();
          default -> "500 Internal Server Error";
        } : Response.CREATED_USER.formatWithData(result.value());
      }

      case "JOIN" -> {
        if (command.args().isEmpty()) {
          yield "400 BAD_REQUEST Invalid group name";
        }
        var result = joinGroup(currentUser, command.args().get(0));
        yield result.isError() ? switch (result.error()) {
          case USER_NOT_REGISTERED -> Response.UNAUTHORIZED.format();
          case ALREADY_IN_GROUP -> Response.CONFLICT_GROUP.formatWithData(command.args().get(0));
          default -> "500 Internal Server Error";
        } : Response.CREATED_USER.formatWithData("Joined group " + result.value());
      }

      case "LEAVE" -> {
        if (command.args().isEmpty()) {
          yield "400 BAD_REQUEST Invalid group name";
        }
        var result = leaveGroup(currentUser, command.args().get(0));
        yield result.isError() ? switch (result.error()) {
          case USER_NOT_REGISTERED -> Response.UNAUTHORIZED.format();
          case NOT_IN_GROUP -> Response.NOT_FOUND_GROUP.formatWithData(command.args().get(0));
          default -> "500 Internal Server Error";
        } : Response.OK.formatWithData("Left group " + result.value());
      }

      case "BROADCAST" -> {
        if (command.args().size() < 2) {
          yield "400 BAD_REQUEST Invalid message format";
        }
        var result = broadcastMessage(currentUser, command.args().get(1));
        yield result.isError() ? switch (result.error()) {
          case USER_NOT_REGISTERED -> Response.UNAUTHORIZED.format();
          default -> "500 Internal Server Error";
        } : Response.OK.formatWithData("Message broadcasted");
      }

      case "GROUPCAST" -> {
        if (command.args().size() < 3) {
          yield "400 BAD_REQUEST Invalid message format";
        }
        var result = groupcastMessage(currentUser, command.args().get(0), command.args().get(2));
        yield result.isError() ? switch (result.error()) {
          case USER_NOT_REGISTERED -> Response.UNAUTHORIZED.format();
          case NOT_IN_GROUP -> Response.FORBIDDEN_GROUP.formatWithData(command.args().get(0));
          default -> "500 Internal Server Error";
        } : Response.OK.formatWithData("Message sent to group " + command.args().get(0));
      }

      case "LIST_USERS" -> {
        var userList = String.join(" ", users.keySet());
        yield Response.OK.formatWithData(users.size() + " " + userList);
      }

      case "LIST_GROUP" -> {
        if (command.args().isEmpty()) {
          yield "400 BAD_REQUEST Invalid group name";
        }
        var groupName = command.args().get(0);
        var userGroupSet = userGroups.get(currentUser);
        if (currentUser == null || userGroupSet == null || !userGroupSet.contains(groupName)) {
          yield Response.FORBIDDEN_GROUP.formatWithData(groupName);
        }
        var group = groups.get(groupName);
        if (group == null) {
          yield Response.NOT_FOUND_GROUP.formatWithData(groupName);
        }
        var memberList = String.join(" ", group.members());
        yield Response.OK.formatWithData(groupName + " " + group.members().size() + " " + memberList);
      }

      default -> "400 BAD_REQUEST Unknown command";
    };
  }

  public void start() {
    try (var serverSocket = new ServerSocket(PORT)) {
      System.out.println("ChatServer started on port " + PORT);

      while (true) {
        var clientSocket = serverSocket.accept();
        new Thread(() -> handleClient(clientSocket)).start();
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    new ChatServer().start();
  }
}
