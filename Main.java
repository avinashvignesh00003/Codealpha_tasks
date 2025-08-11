import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame { // Main class for the GUI application

    // --- GUI Components ---
    private JTextArea chatArea;        // Displays the conversation history
    private JTextField inputField;     // User's input field
    private JButton sendButton;        // Button to send messages
    private JScrollPane scrollPane;    // Scroll pane for the chat area

    // --- Chatbot Logic Instance ---
    private SimpleChatbot chatbot;

    // --- Constructor ---
    public Main() {
        // Initialize the JFrame
        super("AI Chatbot"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setSize(500, 600); // Set initial size of the window
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize the chatbot logic
        chatbot = new SimpleChatbot();

        // Set up the main layout
        setLayout(new BorderLayout(10, 10)); // BorderLayout with padding

        // --- Chat Area (Center) ---
        chatArea = new JTextArea();
        chatArea.setEditable(false); // Make it read-only
        chatArea.setLineWrap(true);   // Wrap long lines
        chatArea.setWrapStyleWord(true); // Wrap at word boundaries
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        scrollPane = new JScrollPane(chatArea); // Add scrollability
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding around chat area
        add(scrollPane, BorderLayout.CENTER);

        // --- Input Panel (South) ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5)); // Panel for input field and send button
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for input
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font for button

        inputPanel.add(inputField, BorderLayout.CENTER); // Input field takes most space
        inputPanel.add(sendButton, BorderLayout.EAST);   // Send button on the right

        add(inputPanel, BorderLayout.SOUTH);

        // --- Event Handling ---
        // ActionListener for the Send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // ActionListener for pressing Enter in the input field
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Display a welcome message when the application starts
        chatArea.append("Chatbot: Hello! How can I help you today?\n");
    }

    /**
     * Handles sending a message:
     * 1. Gets user input.
     * 2. Displays user message in chat area.
     * 3. Gets chatbot response.
     * 4. Displays chatbot response in chat area.
     * 5. Clears input field.
     * 6. Scrolls chat area to the bottom.
     */
    private void sendMessage() {
        String userMessage = inputField.getText().trim(); // Get text and trim whitespace

        if (!userMessage.isEmpty()) { // Only process if message is not empty
            chatArea.append("You: " + userMessage + "\n"); // Append user's message

            String botResponse = chatbot.getResponse(userMessage); // Get response from chatbot
            chatArea.append("Chatbot: " + botResponse + "\n"); // Append chatbot's response

            inputField.setText(""); // Clear the input field
            // Scroll to the bottom of the chat area
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }

    // --- Main Method ---
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true); // Create and show the main window
            }
        });
    }
}

/**
 * SimpleChatbot class implements rule-based responses.
 * It "learns" by having predefined questions/keywords mapped to answers.
 */
class SimpleChatbot {
    private Map<String, String> knowledgeBase;

    public SimpleChatbot() {
        knowledgeBase = new HashMap<>();
        // Training data (frequently asked questions and their answers)
        knowledgeBase.put("hello", "Hi there! How can I assist you?");
        knowledgeBase.put("hi", "Hello! What's on your mind?");
        knowledgeBase.put("how are you", "I'm a bot, so I don't have feelings, but I'm ready to help!");
        knowledgeBase.put("name", "I am a simple AI chatbot created to answer your questions.");
        knowledgeBase.put("help", "I can answer questions about general topics. Try asking me about the weather, my name, or how I work.");
        knowledgeBase.put("weather", "I don't have real-time weather data, but I can tell you it's always sunny in my digital world!");
        knowledgeBase.put("time", "I don't have access to the current time directly, but you can check your device's clock!");
        knowledgeBase.put("thank you", "You're welcome! Is there anything else I can do?");
        knowledgeBase.put("bye", "Goodbye! Have a great day!");
        knowledgeBase.put("what can you do", "I can respond to simple questions based on my knowledge base. Try asking about my capabilities, the weather, or just say hello!");
        knowledgeBase.put("who created you", "I was created by a large language model from Google.");
        knowledgeBase.put("java", "Java is a popular, high-level, class-based, object-oriented programming language.");
        knowledgeBase.put("programming", "Programming is the process of creating a set of instructions that tell a computer how to perform a task.");
        knowledgeBase.put("computer", "A computer is an electronic device that manipulates information, or data. It has the ability to store, retrieve, and process data.");
    }

    /**
     * Gets a response based on the user's message.
     * It converts the message to lowercase and checks for keywords.
     * @param userMessage The message from the user.
     * @return A relevant response from the chatbot.
     */
    public String getResponse(String userMessage) {
        String lowerCaseMessage = userMessage.toLowerCase();

        // Implement simple keyword matching for "NLP"
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerCaseMessage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default response if no keyword matches
        return "I'm not sure how to respond to that. Can you rephrase or ask something else?";
    }
}
