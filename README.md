OVERVIEW
-----------------------
Welcome to Single Player Poker!

The concept is simple: How can I play poker by myself? 
Obviously, the answer is with a fully fleshed out machine learning AI program, so that's what I made.

The "bots" that you play against in-game learn how to play against you as you play against them. The more they win with a certain hand, the more likely they are to bet and raise with that hand in the future, and vice versa.
This data is written to and from text files, so the learning is conserved between play sessions.

In terms of the player's experience, you get to see your cards and can choose how to take your turn. Will you check or bet? Will you call, raise, or fold?
You then see how the bots take their turns, and can take your next turn, and so on until the end of the hand.
You can play hands until you choose to end the program at the end of a hand, or you run out of chips.

The beginning of the program lets you customize your game:
- number of players (2-9)
- starting stack size
- big blind size (small blind automatically 1/2 of a big blind)
- ante size
The default options are:
- 5 bots, 1 player
- 100
- 2
- 2

Note: each player count has its own data file, so the AI's "knowledge" only applies to the player count you play with.
(ex: if you play 35 billion hands with 6 players, the AI will be pretty optimized at 6 players, but if you play again and choose 5 players, the AI will have none of that knowledge)

HOW TO RUN
-----------------------
Run Client.java in your favorite Java code runner to play the game
If there's a problem with the AI, deleting "2-players.txt" and all of those similar files and then running AITrainer.java will give them the base numbers and reset their "knowledge"

TAKING A TURN
-----------------------
Press Enter on your keyboard after each AI action to see what they did, then it will present you with the info you have for the current hand before asking for your turn.
The game will tell you your options and the range of chips you can put in the pot with each action. When it asks for your input, just type the word of your action (ex: "bet")
Then if you need to choose a bet sizing, the game will ask for your size. Now you can put in your bet size.


-----------------------
Good luck fellow gamblers!
