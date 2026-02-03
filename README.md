# Peer-to-Peer Message Sender

A Java-based peer-to-peer messaging application that enables direct communication between peers over TCP sockets. The application follows clean code principles and adheres to SOLID design principles for maintainability and extensibility.

## Features

- **Peer-to-Peer Communication**: Direct message exchange between peers without a central server
- **Connection Management**: Automatic handling of connection lifecycle (setup, active, closed states)
- **Multi-threaded Architecture**: Concurrent handling of multiple connections using thread pools
- **Protocol-based Messaging**: Built-in protocol for connection handshake and message routing
- **JSON Message Format**: Messages are serialized to JSON for structured data exchange
- **Graceful Shutdown**: Clean connection cleanup and resource management

## How It Works

### Starting the Application

Run the application with a port number as an argument:

```bash
java -jar peertopeer-message-sender.jar <port>
```

### Available Commands

| Command | Description |
|---------|-------------|
| `!hello,<hostname:port>` | Initiate a connection to a peer |
| `!ack,<hostname:port>` | Accept an incoming connection request |
| `!bye,<hostname:port>` | Close a connection with a peer |
| `<hostname:port>,<message>` | Send a message to a connected peer |
| `!list` | Display all current connections and their states |
| `!byebye` | Shutdown the application |

### Connection Flow

1. **Initiator** sends `!hello,<peer>` to request a connection
2. **Receiver** sees the incoming connection and can accept with `!ack,<peer>`
3. Once connected, peers can exchange messages freely
4. Either peer can terminate with `!bye,<peer>`

## Project Structure

```
src/main/java/com/example/
├── PeerToPeerApplication.java    # Main entry point
├── lifecycle/
│   └── ApplicationLifecycle.java # Manages application startup/shutdown
├── listener/
│   └── PeerToPeerListener.java   # Listens for incoming connections
├── sender/
│   └── PeerToPeerSender.java     # Handles outgoing messages
├── reader/
│   ├── PeerToPeerReader.java     # Reads incoming messages from peers
│   └── StdinReader.java          # Reads user input from console
├── manager/
│   ├── ConnectionManager.java    # Manages all peer connections
│   ├── ReaderManager.java        # Interface for reader management
│   ├── ReaderCoordinator.java    # Coordinates reader threads
│   ├── SenderManager.java        # Interface for sender management
│   └── SenderCoordinator.java    # Coordinates sender threads
├── protocol/
│   └── ProtocolHandler.java      # Handles message protocol logic
├── message/
│   └── MessageMapper.java        # JSON serialization/deserialization
├── model/
│   ├── Connection.java           # Connection data model
│   ├── ConnectionState.java      # Connection state enumeration
│   └── Packet.java               # Message packet model
└── datastructure/
    ├── ConnectionMap.java        # Interface for connection storage
    ├── ConcurrentConnectionMap.java  # Thread-safe connection storage
    ├── ReaderSet.java            # Interface for reader storage
    └── ConcurrentReaderSet.java  # Thread-safe reader storage
```

## SOLID Principles Implementation

### Single Responsibility Principle (SRP)

Each class has one specific responsibility:

- **`PeerToPeerListener`**: Only listens for incoming connections
- **`PeerToPeerSender`**: Only handles sending messages
- **`PeerToPeerReader`**: Only reads incoming messages
- **`StdinReader`**: Only reads user input from console
- **`ConnectionManager`**: Only manages connection lifecycle
- **`MessageMapper`**: Only handles JSON serialization/deserialization
- **`ProtocolHandler`**: Only handles protocol logic (hello, ack, bye)
- **`ApplicationLifecycle`**: Only manages application startup and shutdown

### Open/Closed Principle (OCP)

The design is open for extension but closed for modification:

- **`ConnectionMap` interface**: New storage implementations can be added (e.g., `ConcurrentConnectionMap`) without modifying existing code
- **`ReaderManager` interface**: Different reader management strategies can be implemented
- **`SenderManager` interface**: Alternative sender strategies can be added
- **`ProtocolAction` enum**: New protocol actions can be added by extending the enum and handling them in the switch statement

### Liskov Substitution Principle (LSP)

Implementations can be substituted for their interfaces:

- `ConcurrentConnectionMap` can replace any `ConnectionMap` reference
- `ReaderCoordinator` can replace any `ReaderManager` reference
- `SenderCoordinator` can replace any `SenderManager` reference
- `ConcurrentReaderSet` can replace any `ReaderSet` reference

### Interface Segregation Principle (ISP)

Interfaces are focused and cohesive:

- **`ConnectionMap`**: Only defines connection storage operations
- **`ReaderManager`**: Only defines reader lifecycle operations (`startNewReader`, `removeReader`)
- **`SenderManager`**: Only defines sender operations (`startSender`)
- **`ReaderSet`**: Only defines reader set operations

### Dependency Inversion Principle (DIP)

High-level modules depend on abstractions:

- `ConnectionManager` depends on `ReaderManager` interface, not `ReaderCoordinator` implementation
- `PeerToPeerApplication` uses `SenderManager` interface for sending messages
- `ConnectionManager` uses `ConnectionMap` interface for storage, not the concrete `ConcurrentConnectionMap`

## Clean Code Practices

### Meaningful Names

- Class names clearly describe their purpose (`PeerToPeerListener`, `ConnectionManager`, `ProtocolHandler`)
- Method names are verbs that describe actions (`startSender`, `markAccepted`, `cleanupDeadConnections`)
- Variable names are self-explanatory (`connectionsByName`, `readerManager`, `connectionName`)

### Small, Focused Methods

Methods are kept small with single purposes:
- `handleHello()`, `handleAck()`, `handleBye()` each handle one protocol command
- `gracefulShutdown()` handles only the cleanup logic
- `generateName()` only creates connection names from socket info

### Use of Modern Java Features

- **Records**: `Connection`, `Packet`, `PeerToPeerReader`, `PeerToPeerSender`, `ProtocolHandler` use Java records for immutable data carriers
- **Enums**: `ConnectionState` and `ProtocolAction` use enums for type-safe constants
- **Optional**: `createConnection()` returns `Optional<String>` for null-safety
- **Stream API**: Used for collection operations (filtering, mapping)
- **Switch Expressions**: Modern switch syntax for cleaner control flow

### Thread Safety

- `ConcurrentHashMap` for thread-safe connection storage
- `ExecutorService` for managed thread pools
- Synchronized blocks for critical sections
- Proper shutdown handling with `shutdownNow()`

### Error Handling

- Specific exception types caught and handled appropriately
- Graceful degradation on errors
- Logging at appropriate levels (debug, info, warn, error, fatal)

### Separation of Concerns

- **Models**: Pure data classes (`Connection`, `Packet`, `ConnectionState`)
- **Managers**: Coordinate between components
- **Readers/Senders**: Handle I/O operations
- **Protocol**: Business logic for message handling
- **Data Structures**: Encapsulate storage details

### Logging

- Consistent use of Log4j2 for structured logging
- Different log levels for different message types
- Debug logs for development, info for operations, warn/error for issues

## Dependencies

- **Java 17+**: Uses modern Java features (records, switch expressions)
- **Jackson**: JSON serialization/deserialization
- **Log4j2**: Structured logging

## License

This project is provided as-is for educational and demonstration purposes.
