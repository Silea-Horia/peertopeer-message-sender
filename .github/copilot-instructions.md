# Copilot Instructions for Peer-to-Peer Message Sender

## Project Overview
This is a peer-to-peer message sender application written in Java. The application allows multiple peers to connect and exchange messages over a network using a custom protocol.

## Architecture
The project follows a modular architecture with the following key components:

### Package Structure
- `com.example.manager`: Connection, reader, and sender management
  - `ConnectionManager`: Manages peer connections
  - `ReaderCoordinator`: Coordinates message reading
  - `SenderCoordinator`: Coordinates message sending
- `com.example.model`: Data models (Connection, Packet, ConnectionState)
- `com.example.listener`: Network listeners for peer connections
- `com.example.sender`: Message sending logic
- `com.example.reader`: Message reading logic (StdinReader, PeerToPeerReader)
- `com.example.message`: Message mapping and transformation
- `com.example.protocol`: Protocol handling
- `com.example.datastructure`: Thread-safe data structures for concurrent operations
- `com.example.lifecycle`: Application lifecycle management

### Key Classes
- `PeerToPeerApplication`: Main entry point, requires a port number as argument
- `ApplicationLifecycle`: Manages application startup and shutdown
- `ConnectionManager`: Central hub for managing peer connections
- `ProtocolHandler`: Handles the communication protocol

## Development Guidelines

### Code Style
- Follow Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Prefer composition over inheritance
- Use the Logger (log4j2) for all logging instead of System.out

### Logging
- Use log4j2 for logging (configured in `src/main/resources/log4j2.xml`)
- Available log levels: FATAL, ERROR, WARN, INFO, DEBUG
- Always log errors with appropriate context
- Use appropriate log levels (INFO for important events, DEBUG for detailed information)

### Thread Safety
- Use thread-safe implementations from the `datastructure` package
- `ConcurrentConnectionMap` and `ConcurrentReaderSet` provide thread-safe operations
- Be cautious with shared state and use proper synchronization when needed

### Error Handling
- Validate input arguments (e.g., port numbers must be numeric)
- Use try-catch blocks for operations that may throw exceptions
- Log errors before exiting or throwing exceptions
- Provide meaningful error messages

### Testing
- Write unit tests for new functionality
- Test edge cases and error conditions
- Ensure thread-safe code is properly tested for concurrent scenarios

## Building and Running

### Building
This project uses standard Java compilation. The main class is `com.example.PeerToPeerApplication`.

### Running
The application requires a port number as a command-line argument:
```bash
java com.example.PeerToPeerApplication <port>
```

### Dependencies
- log4j2 for logging
- Java standard library for networking (java.net)

## Common Tasks

### Adding a New Feature
1. Identify the appropriate package for your feature
2. Follow the existing architectural patterns
3. Use the manager pattern for coordination logic
4. Ensure thread safety if the feature involves concurrent operations
5. Add appropriate logging
6. Update this documentation if needed

### Modifying the Protocol
1. Update `ProtocolHandler` class
2. Ensure backward compatibility if needed
3. Update related message handling in readers and senders
4. Test with multiple peers

### Adding New Data Structures
1. Place in the `datastructure` package
2. Provide both interface and concurrent implementation
3. Follow the pattern of existing structures (e.g., ReaderSet/ConcurrentReaderSet)

## Important Notes
- The application is designed for concurrent operation with multiple peers
- Always consider thread safety when modifying shared state
- The lifecycle management ensures proper cleanup on shutdown
- Port numbers must be valid (1-65535)
- The `myexamples` package contains example implementations for reference

## When Working on This Project
1. **Preserve existing functionality**: Don't break working features
2. **Follow established patterns**: Use the same architectural style as existing code
3. **Test thoroughly**: Verify changes work with multiple concurrent connections
4. **Document changes**: Update comments and this file for significant changes
5. **Use appropriate abstractions**: Leverage existing managers and coordinators
