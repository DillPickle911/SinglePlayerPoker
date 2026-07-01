import java.util.*;
import java.io.*;
import java.lang.*;

public class AiTrainer
{
   public static ArrayList<Integer>[][] data = new ArrayList[13][13];
   public static ArrayList<Card> river = new ArrayList();
   public static ArrayList<PokerPlayer> players = new ArrayList();
   
   public static void main(String[] args)
   {
      Deck deck = new Deck();
      ArrayList<Card> muck = new ArrayList();
      
      for(int numPlayers = 2; numPlayers <= 9; numPlayers++)
      {
         data = new ArrayList[13][13];
         players = new ArrayList();
         
         for(int i = 0; i < numPlayers; i++)
         {
            players.add(new PokerPlayer("Bot " + i, 100));
         }
         
         for(int r = 0; r < data.length; r++)
         {
            for(int c = 0; c < data[0].length; c++)
            {
               data[r][c] = new ArrayList();
            }
         }
         for(int h = 0; h < 1000000; h++)
         {
            // shuffle and deal
            deck.shuffle();
            deck.shuffle();
            deck.shuffle();
            deck.shuffle();
            deck.shuffle();
            deck.shuffle();
            deck.shuffle();
            for(int i = 0; i < players.size(); i++)
            {
               players.get(i).addToHand(deck.deal());
               players.get(i).addToHand(deck.deal());
               players.get(i).setInHand(true);
            }
            // create community cards
            muck.add(deck.deal());
            river.add(deck.deal());
            river.add(deck.deal());
            river.add(deck.deal());
            muck.add(deck.deal());
            river.add(deck.deal());
            muck.add(deck.deal());
            river.add(deck.deal());                                                                                                             
            // declare winner
            ArrayList<PokerPlayer> tiedPlayers = new ArrayList();
            int winner = 0;
               
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
                     for(int k = 0; k < tiedPlayers.size(); k++)
                     {
                        if(tiedPlayers.get(k).getName().equals(players.get(winner).getName()))
                        {
                           tiedPlayers.remove(k);
                           break;
                        }
                           
                     }
                     int temp = Client.breakTie(winner, i);
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
            
            // add data to the data array
            for(int i = 0; i < players.size(); i++)
            {
               ArrayList<Card> hand = players.get(i).getHand();
               // if the second card is greater than the first, make it the first
               if(hand.get(1).getValue() > hand.get(0).getValue())
                  hand.add(hand.remove(0));
               
               // find index in data array
               int row = -1;
               int col = -1;
               if(hand.get(0).getSuit().equals(hand.get(1).getSuit())) // suited cards
               {
                  row = hand.get(0).getValue() - 2;
                  col = hand.get(1).getValue() - 2;
               }
               else // unsuited cards
               {
                  row = hand.get(1).getValue() - 2;
                  col = hand.get(0).getValue() - 2;
               }
               if(winner == i)
                  AiTrainer.data[row][col].add(1);
               else
                  AiTrainer.data[row][col].add(0);
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
         }// end of repeating each hand
         
         writeToFile(numPlayers + "-players.txt");
      }// end of for loop for player numbers
   }// end of main method
   
   public static void writeToFile(String name)
   {
      // copy paste from w3schools
      try (FileWriter myWriter = new FileWriter(name)) {
         for(int r = 0; r < data.length; r++)
         { 
            for(int c = 0; c < data[0].length; c++)
            {
               // for each ArrayList in the 2D array, write out the win percentage by averaging all of the data values in that ArrayList
               double sum = 0.0;
               for(int i = 0; i < AiTrainer.data[r][c].size(); i++)
               {
                  sum += AiTrainer.data[r][c].get(i);
               }
               String winPercent = String.valueOf(sum / AiTrainer.data[r][c].size() * 100);
               if(winPercent.substring(winPercent.indexOf(".")).length() >= 3)
                  myWriter.write(winPercent.substring(0, winPercent.indexOf(".") + 3) + "\n");
               else
                  myWriter.write(winPercent + "\n");
            }
         }
         System.out.println("Successfully wrote " + name + ".");
      } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
      }
      // end of copy paste from w3schools
   }
   
   public static ArrayList<Card> getRiver()
   {
      return river;
   }
}// end of class