package com.example.myexamples;

public class KnockKnockProtocol {
    private enum State {
        WAITING,
        SENT_KNOCK_KNOCK,
        SENT_CLUE,
        ANOTHER,
    }

    private static final int NUM_JOKES = 5;

    private State state = State.WAITING;
    private int currentJoke = 0;

    private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
    private String[] answers = { "Turnip the heat, it's cold in here!",
            "I didn't know you could yodel!",
            "Bless you!",
            "Is there an owl in here?",
            "Is there an echo in here?" };

    public String processInput(String input) {
        var output = "";

        switch (state) {
            case WAITING -> {
                output = "Knock! Knock!";
                state = State.SENT_KNOCK_KNOCK;
            }
            case SENT_KNOCK_KNOCK -> {
                if (input.equalsIgnoreCase("Who's there?")) {
                    output = clues[currentJoke];
                    state = State.SENT_CLUE;
                } else {
                    output = "You're supposed to say " +
                            "\"Who's there?\"! " +
                            "Try again. Knock! Knock!";
                }
            }
            case SENT_CLUE -> {
                if (input.equalsIgnoreCase(clues[currentJoke] + " who?")) {
                    output = answers[currentJoke] + " Want another? (y/n)";
                    state = State.ANOTHER;
                } else {
                    output = "You're supposed to say \"" +
                            clues[currentJoke] +
                            " who?\"" +
                            "! Try again. Knock! Knock!";
                    state = State.SENT_KNOCK_KNOCK;
                }
            }
            case ANOTHER -> {
                if (input.equalsIgnoreCase("y")) {
                    output = "Knock! Knock!";
                    if (currentJoke == NUM_JOKES - 1) {
                        currentJoke = 0;
                    } else {
                        currentJoke++;
                    }
                    state = State.SENT_KNOCK_KNOCK;
                } else {
                    output = "Bye.";
                    state = State.WAITING;
                }
            }
        }

        return output;
    }
}
