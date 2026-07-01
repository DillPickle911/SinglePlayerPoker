import java.util.*;
import java.lang.*;
import java.io.*;

public class Client
{  
   public static final double ONE = 12;
   public static final double TWO = 10;
   public static final double THREE = 8;
   public static final double FOUR = 6.5;
   public static final double FIVE = 5;
   public static final double SIX = 4;
   public static final double SEVEN = 3;
   public static final double EIGHT = 2;
   
   private static int numAI, startingStack, playerIndex, pot, smallBlind, bigBlind, bbSize, ante, deadMoney; //starting chip amount, index of player, size of pot, index of small blind, index of big blind, big blind amount, ante amount, dead money to put in the main pot if there is a side pot
   private static ArrayList<PokerPlayer> players;
   private static ArrayList<PokerPlayer> tiedPlayers = new ArrayList<PokerPlayer>();
   private static ArrayList<Card> river;
   private static Scanner input;
   private static ArrayList<Double>[][] data;
   
   public static void main(String[] arg)
   {
      /*
      2, 3, 4, 5, 6, 7, 8, 9, T, J, Q,  K,  A
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
      
      Hearts, Diamonds, Clubs, Spades
      0,      1,        2,     3
      */
      /* TESTER WITHOUT GAMEPLAY
      PokerPlayer test = new PokerPlayer("Test", 1);
      river = new ArrayList<Card>();
      test.addToHand(new Card(5, 0));
      test.addToHand(new Card(12, 1));
      river.add(new Card(9, 0));
      river.add(new Card(9, 1));
      river.add(new Card(10, 2));
      river.add(new Card(11, 3));
      river.add(new Card(12, 0));
      System.out.println(test.getScoringHand());
      System.exit(0);
      //(ex: A, Q, Q, J, 10, 10, 5)
      */
      
      
      input = new Scanner(System.in);
      
      
      System.out.println("Welcome! Answer the next couple of questions to set up the game. If you type an empty line, the \"default\" option will be picked.\n");
      // asking for player's name
      System.out.println("What's your name? (default: Player)     ");
      String username = input.nextLine();
      if(username.equals(""))
         username = "Player";
      while(username.indexOf("Bot ") == 0) {
         System.out.println("That is not an allowed name. Please try again.");
         System.out.println("\nWhat's your name?     ");
         username = input.nextLine();
         if(username.equals(""))
            username = "Player";
      }
      
      // asking for how many AI will be in the game
      System.out.println("\nHow many other players would you like to play with? (1-8) (default: 5)     ");
      String def = input.nextLine();
      if(def.equals(""))
         def = "5";
      numAI = Integer.parseInt(def);
      while(numAI < 1 || numAI > 8) {
         System.out.println("That is not a valid number of other players. Please enter a number between 1 and 8.");
         System.out.println("\nHow many other players would you like to play with? (1-8)     ");
         def = input.nextLine();
         if(def.equals(""))
            def = "5";
         numAI = Integer.parseInt(def);
      }
      
      // asking for starting stack
      System.out.println("\nHow many chips should each player start with? (at least 100) (default: 100)     ");
      def = input.nextLine();
      if(def.equals(""))
         def = "100";
      startingStack = Integer.parseInt(def);
      while(startingStack < 100) {
         System.out.println("That is not a valid number of starting chips, please try again.");
         System.out.println("\nHow many chips should each player start with? (at least 100)     ");
         def = input.nextLine();
         if(def.equals(""))
            def = "100";
         startingStack = Integer.parseInt(def);
      }
            
      // asking for BB size
      System.out.println("\nHow many chips should one big blind be? (at least 2) (default: 2% of starting stack)     ");
      def = input.nextLine();
      if(def.equals(""))
         def = (startingStack / 50) + "";
      bbSize = Integer.parseInt(def); // sets BB amount
      while(bbSize > startingStack || bbSize <= 1) {
         System.out.println("That is not a valid big blind size, please try again.");
         System.out.println("\nHow many chips should one big blind be? (recommended 1/100th of the starting chip amount)     ");
         def = input.nextLine();
         if(def.equals(""))
            def = (startingStack / 100) + "";
         bbSize = Integer.parseInt(def);
      }
      int sbSize = bbSize / 2;
      
      // asking for ante size
      System.out.println("\nHow many chips should the big blind's ante be? (at least 1 BB) (default: 1 BB)     ");
      def = input.nextLine();
      if(def.equals(""))
         def = bbSize + "";
      ante = Integer.parseInt(def); // sets BB amount
      while(ante < bbSize) {
         System.out.println("That is not a valid ante size, please try again.");
         System.out.println("\nHow many chips should the big blind's ante be? (must be at least as much as the big blind)     ");
         def = input.nextLine();
         if(def.equals(""))
            def = bbSize + "";
         ante = Integer.parseInt(def); // sets BB amount
      }
      
      // instructions
      System.out.println("\nIn order to clearly see what your opponents are doing, you will have to press Enter after each line of text. Press Enter to continue.");
      String idontcare = input.nextLine(); // for garbage lines
      
      // setting up the game
      Deck deck = new Deck();
      river = new ArrayList<Card>();
      ArrayList<Card> muck = new ArrayList<Card>();
      bigBlind = (int)(Math.random() * (numAI + 1));
      smallBlind = bigBlind - 1;
      if(smallBlind == -1)
         smallBlind = numAI;
      
      
      // setting up the AI and player
      players = new ArrayList<PokerPlayer>();
      playerIndex = (int)(Math.random() * (numAI + 1));
      
      for(int i = 1; i <= numAI; i++)
      {
         players.add(new PokerPlayer("Bot " + i, startingStack));
      }
      
      players.add(playerIndex, new PokerPlayer(username, startingStack));
      
      // reads in all of the data for the (approximate) percentage at which each set of hole cards wins at each number of players
      data = new ArrayList[13][13];
      File[] files = new File[8];
      files[0] = new File("2-players.txt");
      files[1] = new File("3-players.txt");
      files[2] = new File("4-players.txt");
      files[3] = new File("5-players.txt");
      files[4] = new File("6-players.txt");
      files[5] = new File("7-players.txt");
      files[6] = new File("8-players.txt");
      files[7] = new File("9-players.txt");
      for(int r = 0; r < 13; r++)
      {
         for(int c = 0; c < 13; c++)
         {
            data[r][c] = new ArrayList();
         }
      }
      // fill in values
      for(int i = 0; i < 8; i++)
      {
         int r = 0;
         int c = 0;
         try (Scanner myReader = new Scanner(files[i])) {
            while (myReader.hasNextLine()) {
               data[r][c].add(Double.parseDouble(myReader.nextLine()));
               c++;
               if(c == 13)
               {
                  c = 0;
                  r++;
               }
            }
         } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
         }
      }
      
      // game loop
      while(true)
      {
         // if the player is the last man standing, print a "you win" message
         if(players.size() == 1 && players.get(0).getName().indexOf("Bot ") != 0)
         {
            System.out.println("You have knocked out all of the bots and taken their chips. Congrats, you win!");
            for(int i = 2; i <= 9; i++)
               writeToFile(i + "-players.txt", i);
            System.exit(0);
         }
         
         // shuffle and deal cards, make each player "in the hand" after cards are dealt to them and store their stack size for the start of this hand
         deck.shuffle();
         deck.shuffle();
         for(int i = 0; i < players.size(); i++)
         {
            players.get(i).addToHand(deck.deal());
            players.get(i).addToHand(deck.deal());
            players.get(i).setInHand(true);
            players.get(i).setStackHandStart(players.get(i).getChips());
         }
         
        /* AI/GAMEPLAY TESTER
         players.get(0).addToHand(new Card(12, 0));
         players.get(0).addToHand(new Card(12, 1));
         players.get(1).addToHand(new Card(12, 2));
         players.get(1).addToHand(new Card(12, 3));
         players.get(2).addToHand(new Card(5, 0));
         players.get(2).addToHand(new Card(0, 1));
         players.get(3).addToHand(new Card(5, 0));
         players.get(3).addToHand(new Card(0, 1));
         players.get(4).addToHand(new Card(5, 0));
         players.get(4).addToHand(new Card(0, 1));
         players.get(5).addToHand(new Card(5, 0));
         players.get(5).addToHand(new Card(0, 1));
         deck.add(0, new Card(9, 2)); // river
         deck.add(0, new Card()); // burn
         deck.add(0, new Card(4, 2)); // turn
         deck.add(0, new Card()); // burn
         deck.add(0, new Card(1, 0)); // flop 3
         deck.add(0, new Card(9, 3)); // flop 2
         deck.add(0, new Card(11, 2)); // flop 1
         deck.add(0, new Card()); // burn
        */
         
         System.out.println("\n  Pre-Flop\n------------");
         // set blinds
         
         //small blind
         if(players.get(smallBlind).getChips() > sbSize)
         {
            players.get(smallBlind).bet(sbSize);
            System.out.println("\n" + players.get(smallBlind).getName() + " blindly bets " + sbSize + " chips.");
         }
         else
         {
            int allin = players.get(smallBlind).getChips();
            players.get(smallBlind).bet(allin);
            System.out.println("\n" + players.get(smallBlind).getName() + " blindly goes all in for " + allin + " chips.");
            players.get(smallBlind).setAllIn(true);
         }
         idontcare = input.nextLine(); // requires player key press
         //big blind
         if(players.get(bigBlind).getChips() > bbSize + ante) // if they can put in bb and ante
         {
            players.get(bigBlind).bet(bbSize);
            System.out.print(players.get(bigBlind).getName() + " blindly bets " + bbSize + " chips.");
            players.get(bigBlind).anteBet(ante);
            System.out.println("\n" + players.get(bigBlind).getName() + " also puts in the " + ante + " chip ante.\n");
         }
         else if(players.get(bigBlind).getChips() > bbSize) // if they can only put in bb
         {
            players.get(bigBlind).bet(bbSize);
            System.out.print(players.get(bigBlind).getName() + " blindly bets " + bbSize + " chips.");
            int temp = players.get(bigBlind).getChips();
            players.get(bigBlind).anteBet(temp);
            System.out.println("\n" + players.get(bigBlind).getName() + " puts the rest of their " + temp + " chips in for the ante.");
            players.get(bigBlind).setAllIn(true);
         }
         else // if they can't put in the bb or the ante
         {
            int allin = players.get(bigBlind).getChips();
            players.get(bigBlind).bet(allin);
            System.out.print(players.get(bigBlind).getName() + " blindly goes all in for " + allin + " chips.");
            System.out.println(players.get(bigBlind).getName() + " has no chips remaining for the ante.");
            players.get(bigBlind).setAllIn(true);
         }
         idontcare = input.nextLine(); // requires player key press
         
         // pre-flop
         bettingRound("Pre-Flop");
         // flop
         muck.add(deck.deal());
         river.add(deck.deal());
         river.add(deck.deal());
         river.add(deck.deal());
         
         if(getNumInHand(players) > 1)
         {
            System.out.println("\n  The Flop\n------------");
            for(PokerPlayer player : players)
            {
               player.resetBet();
               player.setFirstAct(true);
            }
            bettingRound("Flop");
         }
         // turn
         muck.add(deck.deal());
         river.add(deck.deal());
         
         if(getNumInHand(players) > 1)
         {
            System.out.println("\n  The Turn\n------------");
            for(PokerPlayer player : players)
            {
               player.resetBet();
               player.setFirstAct(true);
            }
            bettingRound("Turn");
         }
         // river
         muck.add(deck.deal());
         river.add(deck.deal());
         
         if(getNumInHand(players) > 1)
         {
            System.out.println("\n  The River\n-------------");
            for(PokerPlayer player : players)
            {
               player.resetBet();
               player.setFirstAct(true);
            }
            bettingRound("River");
         }
         // showdown
         if(getNumInHand(players) > 1)
         {
            System.out.println("\n  Showdown\n------------\nRiver: ");
            for(Card card : river)
            {
               System.out.println(card.toString());
            }
            System.out.println();
            for(PokerPlayer player : players)
            {
               if(player.getInHand())
               {
                  System.out.println(player.getName() + "'s Cards: " + player.getCard(0) + ", " + player.getCard(1) + " [" + player.getHandType() + "]");
               }
            }
         }                                                                                                                
         // declare winner, give them pot and reset pot
         while(tiedPlayers.size() > 0)
            tiedPlayers.remove(0);
         int winner = 0;
         // sets winner to the index of the first player in the hand
         for(int j = 0; j < players.size(); j++)
         {
            if(players.get(j).getInHand())
            {
               winner = j;
               break;
            }
         }
            
         tiedPlayers.add(players.get(winner));
         for(int i = winner + 1; i < players.size(); i++)
         {
            if(players.get(i).getInHand())
            {     
               if(players.get(i).getHandRank() > players.get(winner).getHandRank())
               {
                  tiedPlayers = new ArrayList();
                  winner = i;
                  tiedPlayers.add(players.get(i));
               }
               else if(players.get(i).getHandRank() == players.get(winner).getHandRank())
               {
                  if(tiedPlayers.size() == 1)
                  {
                     winner = breakTie(winner, i);
                     if(winner == -1)
                        winner = i;
                  }
                  else // there is more than one person with the same hand type as players.get(i)
                  {
                     for(int p = 0; p < tiedPlayers.size(); p++) // remove winner from tied players
                     {
                        if(tiedPlayers.get(p).equals(players.get(winner)))
                        {
                           tiedPlayers.remove(p);
                           break;
                        }
                     }
                     int temp = breakTie(winner, i);
                     // if players.get(i) lost the tie, nothing changes (already happened in the method)
                     // if they tied, all 3+ players should be in tiedPlayers (already happened in the method)
                     
                     if(temp == i) // if players.get(i) won this tie, they should be the only tied player
                     {
                        tiedPlayers = new ArrayList();
                        tiedPlayers.add(players.get(i));
                     }
                  }
               }
            }
         }
         
         
         // clear any dupes from the tiedPlayers array
         for(int i = 0; i < tiedPlayers.size(); i++)
         {
            if(tiedPlayers.size() == 1)
               break;
            for(int j = i + 1; j < tiedPlayers.size(); j++)
            {
               if(tiedPlayers.get(i).getName().equals(tiedPlayers.get(j).getName()))
               {
                  tiedPlayers.remove(j);
                  j--;
               }
            }
         }
         
         if(getNumInHand(players) == 1)
         {
            int w;
            for(int i = 0; i < players.size(); i++)
            {
               if(players.get(i).getInHand())
               {
                  w = i;
                  System.out.println("\n" + players.get(i).getName() + " wins the hand! They win the pot of " + pot + " chips.");
                  players.get(i).bet(-1 * pot);
                  players.get(i).resetBet();
                  if(i != playerIndex)
                  {
                     // increase data field corresponding to the winner's hole cards by 15% (* 1.15), decreasing the amount that the AI folds these hole cards at this player count
                     ArrayList<Card> temp = players.get(i).getHand();
                     if(temp.get(0).getValue() < temp.get(1).getValue())
                        temp.add(temp.remove(0));
                     if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                        data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 1.15);
                     else
                        data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 1.15);
                  }
               }
            }
         }  
         else if(tiedPlayers.size() == 1)
         {
            String type = tiedPlayers.get(0).getHandType();
            System.out.println("\n" + tiedPlayers.get(0).getName() + " wins the hand with a " + type.substring(0, type.indexOf("(") - 1) + "! They win the pot of " + pot + " chips.");
            boolean smallStack = false;
            for(int p = 0; p < players.size(); p++)
            {
               if(players.get(p).getStackHandStart() > tiedPlayers.get(0).getStackHandStart() && players.get(p).getInHand())
                  smallStack = true;
            }
            if(smallStack)
            {
               pot -= (tiedPlayers.get(0).getStackHandStart() * getNumInHand(players) + deadMoney);
               tiedPlayers.get(0).bet(-1 * pot); // gets chips equal to their starting stack * the number of players in the hand (plus dead money)
               tiedPlayers.get(0).resetBet();
               
               
               ArrayList<PokerPlayer> sidePotPlayers = new ArrayList(); // new array with all players except the player who just won the main pot
               for(int i = 0; i < players.size(); i++)
               {
                  if(!players.get(i).equals(tiedPlayers.get(0)))
                     sidePotPlayers.add(players.get(i));
               }
               if(getNumInHand(sidePotPlayers) == 1) // only one other person left in the pot
               {
                  int w;
                  for(int i = 0; i < players.size(); i++)
                  {
                     if(sidePotPlayers.get(i).getInHand())
                     {
                        w = i;
                        System.out.println("\n" + sidePotPlayers.get(i).getName() + " wins the side pot of " + pot + " chips.");
                        sidePotPlayers.get(i).bet(-1 * pot);
                        sidePotPlayers.get(i).resetBet();
                        if(!sidePotPlayers.get(i).equals(players.get(playerIndex)))
                        {
                           // increase data field corresponding to the winner's hole cards by 15% (* 1.15), decreasing the amount that the AI folds these hole cards at this player count
                           ArrayList<Card> temp = players.get(i).getHand();
                           if(temp.get(0).getValue() < temp.get(1).getValue())
                              temp.add(temp.remove(0));
                           if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                              data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 1.15);
                           else
                              data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 1.15);
                        }
                     }
                  }
               }
               else
               {
                  while(tiedPlayers.size() > 0)
                     tiedPlayers.remove(0);
                  winner = 0;
                  // sets winner to the index of the first player in the hand
                  for(int j = 0; j < sidePotPlayers.size(); j++)
                  {
                     if(sidePotPlayers.get(j).getInHand())
                     {
                        winner = j;
                        break;
                     }
                  }
               
                  tiedPlayers.add(sidePotPlayers.get(winner));
                  for(int i = winner + 1; i < sidePotPlayers.size(); i++)
                  {
                     if(sidePotPlayers.get(i).getInHand())
                     {     
                        if(sidePotPlayers.get(i).getHandRank() > sidePotPlayers.get(winner).getHandRank())
                        {
                           tiedPlayers = new ArrayList();
                           winner = i;
                           tiedPlayers.add(sidePotPlayers.get(i));
                        }
                        else if(sidePotPlayers.get(i).getHandRank() == sidePotPlayers.get(winner).getHandRank())
                        {
                           if(tiedPlayers.size() == 1)
                           {
                              winner = breakTie(winner, i);
                              if(winner == -1)
                                 winner = i;
                           }
                           else // there is more than one person with the same hand type as players.get(i)
                           {
                              for(int p = 0; p < tiedPlayers.size(); p++) // remove winner from tied players
                              {
                                 if(tiedPlayers.get(p).equals(sidePotPlayers.get(winner)))
                                 {
                                    tiedPlayers.remove(p);
                                    break;
                                 }
                              }
                              int temp = breakTie(winner, i);
                           // if players.get(i) lost the tie, nothing changes (already happened in the method)
                           // if they tied, all 3+ players should be in tiedPlayers (already happened in the method)
                           
                              if(temp == i) // if players.get(i) won this tie, they should be the only tied player
                              {
                                 tiedPlayers = new ArrayList();
                                 tiedPlayers.add(sidePotPlayers.get(i));
                              }
                           }
                        }
                     }
                  }
               }
               if(tiedPlayers.size() == 1)
               {
                  type = tiedPlayers.get(0).getHandType();
                  System.out.println("\n" + tiedPlayers.get(0).getName() + " wins the side pot of " + pot + " chips.");
                  tiedPlayers.get(0).bet(-1 * pot);
                  tiedPlayers.get(0).resetBet();
               }
               else
               {
                  System.out.println();
                  for(int i = 0; i < tiedPlayers.size() - 1; i++)
                  {
                     System.out.print(tiedPlayers.get(i).getName() + ", ");
                  }
                  System.out.println("and " + tiedPlayers.get(tiedPlayers.size() - 1).getName() + " split the side pot of " + pot + " chips.");
                  for(PokerPlayer player : tiedPlayers)
                  {
                     if(player.getChips() < 0)
                        player.setChips(0);
                     player.bet(-1 * (pot / tiedPlayers.size()));
                     player.resetBet();
                     if(!player.equals(players.get(playerIndex)))
                     {
                     // increase data field corresponding to the winner's hole cards by 15% (* 1.15), decreasing the amount that the AI folds these hole cards at this player count
                        ArrayList<Card> temp = player.getHand();
                        if(temp.get(0).getValue() < temp.get(1).getValue())
                           temp.add(temp.remove(0));
                        if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                           data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 1.15);
                        else
                           data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 1.15);
                     }
                  }
                  pot = 0;
               // decrease data field corresponding to the losers' hole cards by 15% (* 0.85), increasing the amount that the AI folds these hole cards at this player count
                  for(int j = 0; j < players.size(); j++)
                  {
                     boolean w = false;
                     for(int k = 0; k < tiedPlayers.size(); k++)
                     {
                        if(players.get(j).equals(tiedPlayers.get(k)))
                        {
                           w = true;
                           break;
                        }
                     }
                     if(players.get(j).getInHand() && !w && j != playerIndex) // if this bot got to showdown and didn't win the hand
                     {
                        ArrayList<Card> temp = players.get(j).getHand();
                        if(temp.get(0).getValue() < temp.get(1).getValue())
                           temp.add(temp.remove(0));
                        if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                           data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 0.85);
                        else
                           data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 0.85);
                     }
                  }
               }
               sidePotPlayers.get(winner);
               
               
            }
            else
            {
               tiedPlayers.get(0).bet(-1 * pot);
               tiedPlayers.get(0).resetBet();
            }
            if(!tiedPlayers.get(0).equals(players.get(playerIndex)))
            {
               // increase data field corresponding to the winner's hole cards by 15% (* 1.15), decreasing the amount that the AI folds these hole cards at this player count
               ArrayList<Card> temp = tiedPlayers.get(0).getHand();
               if(temp.get(0).getValue() < temp.get(1).getValue())
                  temp.add(temp.remove(0));
               if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                  data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 1.15);
               else
                  data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 1.15);
            }
            // decrease data field corresponding to the losers' hole cards by 15% (* 0.85), increasing the amount that the AI folds these hole cards at this player count
            for(int j = 0; j < players.size(); j++)
            {
               if(players.get(j).getInHand() && !players.get(j).equals(tiedPlayers.get(0)) && j != playerIndex) // if this bot got to showdown and didn't win the hand
               {
                  ArrayList<Card> temp = players.get(j).getHand();
                  if(temp.get(0).getValue() < temp.get(1).getValue())
                     temp.add(temp.remove(0));
                  if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                     data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 0.85);
                  else
                     data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 0.85);
               }
            }
         }
         else
         {
            System.out.println();
            for(int i = 0; i < tiedPlayers.size() - 1; i++)
            {
               System.out.print(tiedPlayers.get(i).getName() + ", ");
            }
            System.out.println("and " + tiedPlayers.get(tiedPlayers.size() - 1).getName() + " win the hand with a " + tiedPlayers.get(0).getHandType() + "! They split the pot of " + pot + " chips.");
            for(PokerPlayer player : tiedPlayers)
            {
               if(player.getChips() < 0)
                  player.setChips(0);
               player.bet(-1 * (pot / tiedPlayers.size()));
               player.resetBet();
               if(!player.equals(players.get(playerIndex)))
               {
                  // increase data field corresponding to the winner's hole cards by 15% (* 1.15), decreasing the amount that the AI folds these hole cards at this player count
                  ArrayList<Card> temp = player.getHand();
                  if(temp.get(0).getValue() < temp.get(1).getValue())
                     temp.add(temp.remove(0));
                  if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                     data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 1.15);
                  else
                     data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 1.15);
               }
            }
            pot = 0;
            // decrease data field corresponding to the losers' hole cards by 15% (* 0.85), increasing the amount that the AI folds these hole cards at this player count
            for(int j = 0; j < players.size(); j++)
            {
               boolean w = false;
               for(int k = 0; k < tiedPlayers.size(); k++)
               {
                  if(players.get(j).equals(tiedPlayers.get(k)))
                  {
                     w = true;
                     break;
                  }
               }
               if(players.get(j).getInHand() && !w && j != playerIndex) // if this bot got to showdown and didn't win the hand
               {
                  ArrayList<Card> temp = players.get(j).getHand();
                  if(temp.get(0).getValue() < temp.get(1).getValue())
                     temp.add(temp.remove(0));
                  if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                     data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].set(players.size() - 2, data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2) * 0.85);
                  else
                     data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].set(players.size() - 2, data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2) * 0.85);
               }
            }
         }   
         
         // end program if player has no chips or chooses to stop playing
         if(players.get(playerIndex).getChips() <= 0) {
            System.out.println("\nShoot, you ran out of chips! Thanks for playing!");
            for(int i = 2; i <= 9; i++)
               writeToFile(i + "-players.txt", i);
            System.exit(0);
         }
            
         String keepPlaying = "";
         while(!keepPlaying.equals("y") && !keepPlaying.equals("n")) {
            System.out.print("\n\nWould you like to continue? (y/n)     ");
            keepPlaying = input.nextLine().toLowerCase();
         }
         if(keepPlaying.equals("n")) {
            System.out.print("\n\nYou left the table with " + players.get(playerIndex).getChips() + " chips. (Profit/Loss: ");
            if(players.get(playerIndex).getChips() - startingStack > 0)
               System.out.print("+" + (players.get(playerIndex).getChips() - startingStack));
            else
               System.out.print((players.get(playerIndex).getChips() - startingStack));
            System.out.println(")\nThanks for playing!");
            for(int i = 2; i <= 9; i++)
               writeToFile(i + "-players.txt", i);
            System.exit(0);
         }
         
         // return cards to deck
         for(int i = 0; i < players.size(); i++)
         {
            deck.restack(players.get(i).removeFromHand());
            deck.restack(players.get(i).removeFromHand());
         }
         for(int j = 0; j < 5; j++)
            deck.restack(river.remove(0));
         deck.restack(muck.remove(0));
         deck.restack(muck.remove(0));
         deck.restack(muck.remove(0));
         
         // if any bot is out of chips, they are removed from the game and then the playerIndex is moved accordingly
         for(int i = 0; i < players.size(); i++)
         {
            if(players.get(i).getChips() <= 0)
            {
               players.remove(i);
               if(i <= bigBlind)
               {
                  bigBlind--;
                  smallBlind--;
               }
               i--;
            }
         }
         for(int j = 0; j < players.size(); j++)
         {
            if(players.get(j).getName().indexOf("Bot ") != 0)
               playerIndex = j;
         }
         
         // set blinds, reset people going all in and reset bets
         bigBlind++;
         smallBlind++;
         if(smallBlind >= players.size())
            smallBlind = players.size() - 1;
         if(bigBlind >= players.size())
            bigBlind = 0;
         for(PokerPlayer player : players)
         {
            player.setAllIn(false);
            player.resetBet();
         }
      }
   }//end of main method
   
   public static void changePot(int amt)
   {
      pot += amt;
   }
   
   public static ArrayList<Card> getRiver()
   {
      return river;
   }
   
   public static int getNumInHand(ArrayList<PokerPlayer> players)
   {
      int count = 0;
      for(PokerPlayer player : players)
      {
         if(player.getInHand())
            count++;
      }
      return count;
   }
   
   public static int breakTie(int first, int second)
   {
      // get each player's scoring hand
      ArrayList<Card> hand1;
      ArrayList<Card> hand2;
      if(players != null)
      {
         hand1 = players.get(first).getScoringHand();
         hand2 = players.get(second).getScoringHand();
      }
      else
      {
         hand1 = AiTrainer.players.get(first).getScoringHand();
         hand2 = AiTrainer.players.get(second).getScoringHand();
      }
      
      // scoring hand is in order greatest to least
      // find where one player has a greater card than the other and declare them the winner
      boolean one = false;
      boolean two = false;
      for(int i = 0; i < hand1.size(); i++)
      {
         if(hand1.get(i).getValue() > hand2.get(i).getValue())
            one = true;
         else if(hand1.get(i).getValue() < hand2.get(i).getValue())
            two = true;
      }
      if(players != null)
      {
         if(one)
         {
            tiedPlayers.add(players.get(first));
            return first;
         }
         else if(two)
         {
            tiedPlayers.add(players.get(second));
            return second;
         }
         else
         {
            tiedPlayers.add(players.get(first));
            tiedPlayers.add(players.get(second));
            return first; // they both have the same hand type so for comparing reasons it doesn't matter which
         }
      }
      else
      {
         if(one)
         {
            tiedPlayers.add(AiTrainer.players.get(first));
            return first;
         }
         else if(two)
         {
            tiedPlayers.add(AiTrainer.players.get(second));
            return second;
         }
         else
         {
            tiedPlayers.add(AiTrainer.players.get(first));
            tiedPlayers.add(AiTrainer.players.get(second));
            return first;
         }
      }
   }
   
   public static void displayGame()
   {
      System.out.println("\n  Chips\n---------");
      for(int i = 0; i < players.size(); i++)
      {
         System.out.print(players.get(i).getName() + "'s Chips: " + players.get(i).getChips());
         if(i == bigBlind)
            System.out.println(" (BB)");
         else if(i == smallBlind)
            System.out.println(" (SS)");
         else
            System.out.println();
      }
      System.out.println("\n  Pot\n-------\n" + pot + " chips");
      
      System.out.println("\n" + players.get(playerIndex).getName() + "'s Cards: " + players.get(playerIndex).getCard(0) + ", " + players.get(playerIndex).getCard(1) + "\n");
      
      if(river.size() > 0)
         System.out.println("\n  Community Cards\n-------------------");
      for(Card card : river)
      {
         System.out.println(card.toString());
      }
   }//end of display game method
   
   public static void bettingRound(String round)
   {
      int currentBet = 0;
      if(round.equals("Pre-Flop"))
         currentBet = bbSize;
      
      for(PokerPlayer player : players)
         player.setFirstAct(true);
      
      int raises = 0; // keeps track of how many times people have raised this betting round
      
      int i = bigBlind + 1;
      while(true)
      {
         if(i == players.size())
            i = 0;
         
         boolean allAllIn = true;
         for(PokerPlayer player : players)
         {
            if(player.getInHand() && !player.getAllIn())
            {
               allAllIn = false;
               break;
            }
         }
         if(allAllIn)
            return; // if all players in the hand are all in, end this betting round
         
         if(players.get(i).getInHand() && !players.get(i).getAllIn())
         {
            int inHand = 0;
            for(PokerPlayer player : players)
            {
               if(player.getInHand())
                  inHand++;
            }
            if(inHand == 1)
               return; // ends the betting round if there's only one person in the hand
            if(players.get(i).getName().indexOf("Bot ") == 0)
            {
               if(currentBet == players.get(i).getCurrentBet() && !players.get(i).getFirstAct())
                  return; // ends the betting round if it's not the bot's first turn and their bet equals the currentBet
               players.get(i).setFirstAct(false);
               
               /********************THIS HERE IS THE PROJECT*********************/
               /*
               pre: it is a bot's turn in the game
               post: based on how strong their hand is and the previous actions taken by the player and other bots, make a decision with some amount of randomness (check, bet, raise, call, fold)
               */
               int random = (int)(Math.random() * 100); // adds some randomness
               String action = "fold"; // tracks which action this player takes
               int bet = 0; // tracks how much this player is betting
               int af = 0; // aggression factor, increases bet sizing
               double hs = players.get(i).getHandStrength(); // hole card strength
               
               double wpF = 0.0; // finds data number from data array, this number will not be fiddled with
               double wp = 0.0; // finds data number from data array, this number will be fiddled with
               ArrayList<Card> temp = players.get(i).getHand();
               if(temp.get(0).getValue() < temp.get(1).getValue())
                  temp.add(temp.remove(0));
               if(temp.get(0).getSuit().equals(temp.get(1).getSuit()))
                  wp = data[13 - temp.get(0).getValue() + 1][13 - temp.get(1).getValue() + 1].get(players.size() - 2);
               else
                  wp = data[13 - temp.get(1).getValue() + 1][13 - temp.get(0).getValue() + 1].get(players.size() - 2);
               wp = (wp / data[0][0].get(players.size() - 2)) * 100; // divides number by AA win percent
               wpF = wp;
               
               //********************************************************
               //ALL OF THESE NUMBERS ARE VERY OPEN TO BEING FIDDLED WITH
               //********************************************************
                      
               if(round.equals("Pre-Flop")) // pre-flop decisions are based on hole cards and number of raises, no bluffs pre-flop
               {
                  // hand strength boosts odds
                  if(hs >= ONE) // premium hands, AKs, JJ, or better
                     wp *= 1.20;
                  else if(hs >= TWO) // great hands, AKo or better
                     wp *= 1.15;
                  else if(hs >= THREE) // pretty good hands, JTs or better
                     wp *= 1.12;
                  else if(hs >= FOUR) // good hands, 98s or better
                     wp *= 1.1;
                  else if(hs >= FIVE) // JTo type shi
                     wp *= 1.08;
                  else if(hs >= SIX) // QTo type shi
                     wp *= 1.06;
                  else if(hs >= SEVEN) // 98o type shi
                     wp *= 1.04;
                  else if(hs >= EIGHT) // 54o type shi
                     wp *= 1.02;
                  else
                     wp *= 0.5;
                  
                  // previous raises decrease odds significantly
                  int random2 = (int)(Math.random() * 100);
                  if(raises >= 4) // 5bet or more
                  {
                     if(hs < THREE && random2 <= 95)
                        wp = 0.0;
                     else
                        wp *= 0.6;
                  }
                  else if(raises == 3) // 4bet
                  {
                     if(hs < FOUR && random2 <= 95)
                        wp = 0.0;
                     else
                        wp *= 0.7;
                  }
                  else if(raises == 2) // 3bet
                  {
                     if(hs < FIVE && random2 <= 95)
                        wp = 0.0;
                     else
                        wp *= 0.8;
                  }
                  else if(raises == 1) // single raise
                  {
                     wp *= 0.9;
                  }
                  // with no raises, there are no penalties
                  
                  if(wp >= 100)
                     wp = 98.7654321;
                  
                  // after fiddling with the number, we make a choice whether to continue or fold
                  if(random <= wp) // continuing
                  {
                     // decide whether to raise or call
                     if(hs >= ONE && random <= (wp * .5)) // of the percentage that this hand continues, 50% chance to raise, 50% chance to call
                     {
                        action = "raise";
                     }
                     else if(hs >= TWO && random <= (wp * .4))
                     {
                        action = "raise";
                     }
                     else if(hs >= THREE && random <= (wp * .3))
                     {
                        action = "raise";
                     }
                     else if(hs >= FOUR && random <= (wp * .2))
                     {
                        action = "raise";
                     }
                     else if(hs >= FIVE && random <= (wp * .15))
                     {
                        action = "raise";
                     }
                     else if(hs >= SIX && random <= (wp * .1))
                     {
                        action = "raise";
                     }
                     else if(hs >= SEVEN && random <= (wp * .08))
                     {
                        action = "raise";
                     }
                     else if(hs >= EIGHT && random <= (wp * .05))
                     {
                        action = "raise";
                     }
                     else
                        action = "call";
                  }
                  else
                     action = "fold";
                  
               }
               else // post-flop decisions are based on hand type, board texture as well as hole cards and number of raises
               {
                  // hand strength boosts odds a little
                  if(hs >= ONE) // premium hands, AKs, JJ, or better
                     wp *= 1.1;
                  else if(hs >= TWO) // great hands, AKo or better
                     wp *= 1.08;
                  else if(hs >= THREE) // pretty good hands, JTs or better
                     wp *= 1.06;
                  else if(hs >= FOUR) // good hands, 98s or better
                     wp *= 1.04;
                  else if(hs >= FIVE) // JTo type shi
                     wp *= 1.02;
                  else if(hs >= SIX) // QTo type shi
                     wp *= 1.01;
                  else if(hs >= SEVEN) // 98o type shi
                     wp *= 1.005;
                  else if(hs >= EIGHT) // 54o type shi
                     wp *= 1.0025;
                  else
                     wp *= 0.95;
                  
                  // previous raises decrease odds some
                  int random2 = (int)(Math.random() * 100);
                  if(raises >= 4) // 5bet or more
                  {
                     if(hs < THREE && random2 <= 80)
                        wp = 5.0;
                     else
                        wp *= 0.8;
                  }
                  else if(raises == 3) // 4bet
                  {
                     if(hs < FOUR && random2 <= 80)
                        wp = 5.0;
                     else
                        wp *= 0.85;
                  }
                  else if(raises == 2) // 3bet
                  {
                     if(hs < FIVE && random2 <= 80)
                        wp = 5.0;
                     else
                        wp *= 0.9;
                  }
                  else if(raises == 1) // single raise
                  {
                     wp *= 0.95;
                  }
                  // with no raises, there are no penalties
                  
                  // being on a draw increases odds by a lot on the flop and by a little on the turn
                  if(players.get(i).onADraw())
                  {
                     if(round.equals("Flop"))
                        wp *= 1.2;
                     else if(round.equals("Turn"))
                        wp *= 1.05;
                  }
                  
                  // 4 cards of the same suit or a 4-liner to a straight or a double paired board decrease odds a bit more
                  if(getBoardHandRank() >= 2 || flushPotential() >= 4 || straightPotential() >= 4)
                  {
                     wp *= 0.85;
                  }
                  // 3 cards of the same suit or a 3-liner to a straight or a paired board decrease odds slightly
                  else if(getBoardHandRank() >= 1 || flushPotential() >= 3 || straightPotential() >= 3)
                  {
                     wp *= 0.95;
                  }
                  
                  int pairType = players.get(i).getPairType();
                  // having top pair or better increases odds significantly on all three streets
                  if(pairType >= 3 || players.get(i).getHandRank() > 1)
                  {
                     wp *= 1.2;
                  }
                  // having a middle pair increases odds on the flop and turn only
                  else if(pairType == 2)
                  {
                     if(round.equals("Flop"))
                        wp *= 1.08;
                     else if(round.equals("Turn"))
                        wp *= 1.04;
                  } 
                  // having bottom pair or an underpair increases odds on the flop only
                  else if(round.equals("Flop") && (pairType == 1 || pairType == 0))
                  {
                     wp *= 1.06;
                  }
                  
                  if(wp >= 100)
                     wp = 98.7654321;
                  
                  // after fiddling with the number, we make a choice whether to continue or fold
                  if((random > wp && random > 95) || players.get(i).getBluffing()) // if we would normally fold here or are already bluffing, we could bluff
                  {
                     int random4 = (int)(Math.random() * 2); // 50% act as if we have the nuts, 50% act as if we have a really strong hand
                     if(random4 == 0)
                        action = "call";
                     if(random4 == 1)
                        action = "raise";
                     // if bluffing, always raise on the river b/c calling always leads to a loss
                     if(round.equals("River"))
                        action = "raise";
                     players.get(i).setBluffing(true);
                  }
                  else if(random <= wp) // continuing
                  {
                     // decide whether to raise or call
                     if(hs >= ONE && random <= (wp * .3)) // of the percentage that this hand continues, 30% chance to raise, 70% chance to call
                     {
                        action = "raise";
                     }
                     else if(hs >= TWO && random <= (wp * .25))
                     {
                        action = "raise";
                     }
                     else if(hs >= THREE && random <= (wp * .2))
                     {
                        action = "raise";
                     }
                     else if(hs >= FOUR && random <= (wp * .15))
                     {
                        action = "raise";
                     }
                     else if(hs >= FIVE && random <= (wp * .1))
                     {
                        action = "raise";
                     }
                     else
                        action = "call";
                  }
                  else
                     action = "fold";
                  if(currentBet == 0) // if our options are check or bet
                  {
                     if(random <= (wp / 2))
                        action = "bet";
                     else
                        action = "check";
                  }
               }
               /***************END OF PROJECT**************************/
               // based on the type stated, set the needed variables accordingly
               if(action.equals("call"))
               {
                  if(i == bigBlind && currentBet == bbSize) // if our options are check or raise, call = check
                  {
                     bet = 0;
                     action = "check";
                  }
                  else
                     bet = currentBet;
               }
               else if(action.equals("bet"))
               {
                  bet = (int)(Math.random() * (pot / 2)) + bbSize + (af * bbSize); // minimum bet is 1 big blind
                  currentBet = bet;
               }
               else if(action.equals("raise"))
               {
                  raises++;
                  bet = (int)(Math.random() * (pot / 2)) + (2 * currentBet) + (af * bbSize); // minimum raise is 2x current bet
                  // round off the number
                  bet /= bbSize;
                  bet *= bbSize;
                  currentBet = bet;
               }
               else // check or fold
               {
                  if(i == bigBlind && currentBet == bbSize) // if our options are check or raise, fold = check
                     action = "check";
                  bet = 0;
               }
               
               // if the bot bet more chips than they have, it bets all of the bots chips instead, putting them all in
               if(bet >= players.get(i).getChips())
               {
                  bet = players.get(i).getChips();
                  if(bet > currentBet)
                  {
                     currentBet = bet;
                     raises++;
                  }
                  action = "all in";
               }
               
               // prints a message detailing this bot's action and bets accordingly
               if(action.equals("check"))
                  System.out.println(players.get(i).getName() + " checks.");
               else if(action.equals("call"))
               {
                  System.out.println(players.get(i).getName() + " calls by putting in " + bet + " chips.");
                  players.get(i).bet(bet);
               }
               else if(action.equals("bet"))
               {
                  System.out.println(players.get(i).getName() + " bets " + bet + " chips.");
                  players.get(i).bet(bet);
               }
               else if(action.equals("raise"))
               {
                  System.out.println(players.get(i).getName() + " raises to " + bet + " chips.");
                  players.get(i).bet(bet - players.get(i).getCurrentBet());
               }
               else if(action.equals("all in"))
               {
                  System.out.println(players.get(i).getName() + " goes all in for " + bet + " chips.");
                  players.get(i).bet(bet);
                  players.get(i).setAllIn(true);
               }
               else
               {
                  System.out.println(players.get(i).getName() + " folds.");
                  players.get(i).setInHand(false);
                  deadMoney += (players.get(i).getStackHandStart() - players.get(i).getChips()); // any chips this player put in the pot before folding go into the main pot if a side pot is created
               }
               System.out.println("   Current Bet: " + currentBet);
               String idontcare = "";
               if(players.get(playerIndex).getInHand()) 
                  idontcare = input.nextLine(); // if player is in the hand, requires player key press
            }    
            else //player's turn
            {
               String choice = "";
            
               if(currentBet == players.get(playerIndex).getCurrentBet() && !players.get(playerIndex).getFirstAct())
                  return; // ends the betting round if it's not the player's first turn and their bet equals the currentBet
                     
               displayGame();
              
               System.out.println("\n\nIf you need to choose your bet sizing, just type 'bet' first, then you will be prompted for your size.\n");
               while(true)
               {
                  boolean check = false;
                  boolean bet = false;
                  boolean call = false;
                  boolean raise = false;
                  boolean fold = false;
                  System.out.print("\nWhat would you like to do? (");
                  // checking the state of the game to print the correct options for the player
                  if(currentBet == players.get(playerIndex).getCurrentBet() && currentBet == bbSize && i == bigBlind)
                  {
                     System.out.print("check or raise (" + (currentBet * 2) + "-" + (currentBet * 2 + pot) + " chips))     ");
                     check = true;
                     raise = true;
                  }
                  else if(currentBet == 0)
                  {
                     System.out.print("check or bet (maximum " + pot + " chips))     ");
                     check = true;
                     bet = true;
                  }
                  else
                  {
                     System.out.print("call (" + (currentBet - players.get(playerIndex).getCurrentBet()) +  " chips), raise (" + (currentBet * 2) + 
                                      "-" + (currentBet * 2 + pot) + " chips), or fold)     ");
                     call = true;
                     raise = true;
                     fold = true;
                  }
                  // user input
                  choice = input.nextLine().toLowerCase();
                  
                  if((check && choice.equals("check")) || (bet && choice.equals("bet")) || (call && choice.equals("call")) || (raise && choice.equals("raise")) || (fold && choice.equals("fold")))
                     break;
                  else
                     System.out.println("\nThat is not a valid option. Please try again.");
               }
               //
               players.get(playerIndex).setFirstAct(false);
               //
               System.out.println();
               int bet = 0;
               
               if(choice.equals("check"))
                  System.out.println(players.get(i).getName() + " checks.");
               else if(choice.equals("call"))
               {
                  if(currentBet >= players.get(i).getChips())
                  {
                     bet = players.get(i).getChips() + players.get(i).getCurrentBet();
                     System.out.println(players.get(i).getName() + " goes all in for " + bet + " chips.");
                     players.get(i).bet(bet);
                     players.get(i).setAllIn(true);
                     //currentBet = bet; the currentBet doesn't change when someone goes all in to call a bet
                  }
                  else
                  {
                     bet = currentBet - players.get(i).getCurrentBet();
                     System.out.println(players.get(i).getName() + " calls by putting in " + bet + " chips.");
                     players.get(i).bet(bet);
                  }
               }
               else if(choice.equals("bet"))
               {
                  while(true)
                  {
                     System.out.print("How much would you like to bet? (maximum " + pot + " chips)     ");
                     bet = input.nextInt();
                     input.nextLine();
                     if(bet <= pot && bet > 0)
                        break;
                     else
                        System.out.println("That is an invalid bet sizing. Please try again.");
                  }
                  if(bet >= players.get(i).getChips())
                  {
                     bet = players.get(i).getChips();
                     System.out.println(players.get(i).getName() + " goes all in for " + bet + " chips.");
                     players.get(i).bet(bet);
                     players.get(i).setAllIn(true);
                     currentBet = bet;
                  }
                  else
                  {
                     System.out.println();
                     System.out.println(players.get(i).getName() + " bets " + bet + " chips.");
                     players.get(i).bet(bet - players.get(i).getCurrentBet());
                     currentBet = bet;
                  }
               }
               else if(choice.equals("raise"))
               {
                  while(true)
                  {
                     System.out.print("How much would you like to raise to? (" + (currentBet * 2) + "-" + (currentBet * 2 + pot) + " chips)     ");
                     bet = input.nextInt();
                     input.nextLine();
                     if(bet >= (currentBet * 2) && bet <= (currentBet * 2 + pot))
                        break;
                     else
                        System.out.println("That is an invalid bet sizing. Please try again.");
                  }
                  if(bet >= players.get(i).getChips())
                  {
                     bet = players.get(i).getChips() + players.get(i).getCurrentBet();
                     System.out.println(players.get(i).getName() + " goes all in for " + bet + " chips.");
                     players.get(i).bet(bet);
                     players.get(i).setAllIn(true);
                     currentBet = bet;
                     raises++;
                  }
                  else
                  {
                     System.out.println(players.get(i).getName() + " raises to " + bet + " chips.");
                     players.get(i).bet(bet - players.get(i).getCurrentBet());
                     currentBet = bet;
                     raises++;
                  }
               }
               else
               {
                  System.out.println(players.get(i).getName() + " folds.");
                  players.get(i).setInHand(false);
                  deadMoney += (players.get(i).getStackHandStart() - players.get(i).getChips()); // any chips put in the pot before folding go into the main pot if a side pot is created
               }
               System.out.println("   Current Bet: " + currentBet + "\n");
            }
         }
       
         i++;
      }
   }//end of betting round method
   
   public static int flushPotential()
   {
      int[] suits = new int[4];
      for(Card card : river)
      {
         if(card.getSuit().equals("Hearts"))
            suits[0]++;
         else if(card.getSuit().equals("Diamonds"))
            suits[1]++;
         else if(card.getSuit().equals("Spades"))
            suits[2]++;
         else if(card.getSuit().equals("Clubs"))
            suits[3]++;
      }
      
      int max = suits[0];
      for(int i = 1; i < 4; i++)
      {
         if(suits[i] > max)
            max = suits[i];
      }
      return max;
   }
   
   public static int straightPotential()
   {
      ArrayList<Card> r = river;
      int max = r.get(0).getValue();
      for(int i = 0; i < r.size() - 1; i++) // sorts the river in descending order
      {
         int l = i + 1;
         int f = -1;
         for(; l < r.size(); l++)
         {
            if(r.get(l).getValue() > max)
            {
               max = r.get(l).getValue();
               f = l;
            }
         }
         if(f != -1)
         {
            Card temp = r.get(i);
            r.set(i, r.get(f));
            r.set(f, temp);
         }
      }
      
      // check for 3-card straight possibilities
      for(int i = 0; i < river.size() - 2; i++)
      {
         int gap1 = river.get(i).getValue() - river.get(i + 1).getValue() - 1;
         int gap2 = river.get(i + 1).getValue() - river.get(i + 2).getValue() - 1;
         if(gap1 + gap2 <= 2)
            return 3;
      }
      
      // check for 3-card straight possibilities
      for(int i = 0; i < river.size() - 3; i++)
      {
         int gap1 = river.get(i).getValue() - river.get(i + 1).getValue() - 1;
         int gap2 = river.get(i + 1).getValue() - river.get(i + 2).getValue() - 1;
         int gap3 = river.get(i + 2).getValue() - river.get(i + 3).getValue() - 1;
         if(gap1 + gap2  + gap3 <= 1)
            return 4;
      }
      return -1;
   }
   
   public static int getBoardHandRank()
   {
       // make rh, an array list of the player's hole cards and the river
      ArrayList<Card> rh = new ArrayList<Card>();
      for(int i = 0; i < Client.getRiver().size(); i++)
         rh.add(Client.getRiver().get(i));
      
      int[] suits = new int[4];
      int[] ranks = new int[13];
      
      // tallying up the number of cards of each suit and rank
      for(Card card : rh)
      {
         if(card.getSuit().equals("Hearts"))
            suits[0]++;
         else if(card.getSuit().equals("Diamonds"))
            suits[1]++;
         else if(card.getSuit().equals("Spades"))
            suits[2]++;
         else if(card.getSuit().equals("Clubs"))
            suits[3]++;
         
         if(card.getValue() == 2)
            ranks[0]++;
         else if(card.getValue() == 3)
            ranks[1]++;
         else if(card.getValue() == 4)
            ranks[2]++;
         else if(card.getValue() == 5)
            ranks[3]++;
         else if(card.getValue() == 6)
            ranks[4]++;
         else if(card.getValue() == 7)
            ranks[5]++;
         else if(card.getValue() == 8)
            ranks[6]++;
         else if(card.getValue() == 9)
            ranks[7]++;
         else if(card.getValue() == 10)
            ranks[8]++;
         else if(card.getValue() == 11)
            ranks[9]++;
         else if(card.getValue() == 12)
            ranks[10]++;
         else if(card.getValue() == 13)
            ranks[11]++;
         else if(card.getValue() == 14)
            ranks[12]++;
      }
      
      int pairs = 0;
      int threesOfAKind = 0;
      int foursOfAKind = 0;
      // counting number of pairs and threes of a kind and fours of a kind for those hands
      for(int rank : ranks)
      {
         if(rank == 4)
            foursOfAKind++;
         if(rank == 3)
            threesOfAKind++;
         if(rank == 2)
            pairs++;
      }
      
      // checks to make sure that if there's a three of a kind and a pair, there are two ranks
      boolean fullHouse = false;
      if(threesOfAKind >= 1 && pairs >= 1)
      {
         int temp = 0;
         for(int rank : ranks)
         {
            if(rank >= 2)
               temp++;
         }
         if(temp > 1)
            fullHouse = true;
      }
      
      boolean straight = false;
      boolean wheel = false;
      boolean straightFlush = false;
      Card[] straightCards = new Card[5];
      // checking for a straight
      // the wheel (A, 2, 3, 4, 5) is a special and separate check
      if(ranks[12] >= 1 && ranks[0] >= 1 && ranks[1] >= 1 && ranks[2] >= 1 && ranks[3] >= 1) 
      {
         straight = true;
         wheel = true;
         straightCards[0] = PokerPlayer.findCard(14, rh);
         straightCards[1] = PokerPlayer.findCard(2, rh);
         straightCards[2] = PokerPlayer.findCard(3, rh);
         straightCards[3] = PokerPlayer.findCard(4, rh);
         straightCards[4] = PokerPlayer.findCard(5, rh);
         
      }
      // other straights
      for(int i = 2; i < ranks.length - 2; i++)
      {
         if(ranks[i - 2] >= 1 && ranks[i - 1] >= 1 && ranks[i] >= 1 && ranks[i + 1] >= 1 && ranks[i + 2] >= 1)
         {
            straight = true;
            for(int j = 0; j < 5; j++)
               straightCards[j] = PokerPlayer.findCard(i + j, rh);
         }
      }
      // if the hand has a straight, check if it is also a flush
      if(straight)
      {
         straightFlush = true;
         for(int k = 0; k < 4; k++)
            if(!straightCards[k].getSuit().equals(straightCards[k + 1].getSuit()))
               straightFlush = false;
      }
      
      // return the type of poker hand based on the cards in rh (NEW ADD: Also return what cards score)
      if(straightFlush && straightCards[4].getValue() == 14 && straightCards[3].getValue() == 13)
         return 9;
      else if(straightFlush)
         return 8;
      else if(foursOfAKind >= 1)
         return 7;
      else if(pairs >= 1 && threesOfAKind >= 1 && fullHouse)
         return 6;
      else if(suits[0] >= 5 || suits[1] >= 5 || suits[2] >= 5 || suits[3] >= 5)
         return 5;
      else if(straight)
         return 4;
      else if(threesOfAKind >= 1)
         return 3;
      else if(pairs >= 2)
         return 2;
      else if(pairs >= 1)
         return 1;
      else
         return 0;
   }
   
   public static void writeToFile(String name, int numPlayers)
   {
      // copy paste from w3schools
      try (FileWriter myWriter = new FileWriter(name)) {
         for(int r = 0; r < data.length; r++)
         { 
            for(int c = 0; c < data[0].length; c++)
            {
               String winPercent = data[r][c].get(numPlayers - 2).toString();
               // write each value in the data array out to the file
               if(winPercent.substring(winPercent.indexOf(".")).length() >= 3)
                  myWriter.write(winPercent.substring(0, winPercent.indexOf(".") + 3) + "\n");
               else
                  myWriter.write(winPercent + "\n");
            }
         }
      } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
      }
      // end of copy paste from w3schools
   }
}//end of class