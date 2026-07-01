import java.util.*;

public class Card
{
   private String name, suit;
   private int value;
   String[] names = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace", "none"};
   String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades", "none"};
   
   public Card()
   {
      name = names[13];
      suit = suits[4];
   }
   
   public Card(int cName, int cSuit)
   {
      // sets name and suit
      name = names[cName];
      suit = suits[cSuit];
      
      // sets value based on what kind of card it is
      value = cName + 2;
   }
   
   public int getValue()
   {
      return value;
   }
   
   public String getSuit()
   {
      return suit;
   }
   
   public String getName()
   {
      return name;
   }
   
   @Override
   public String toString()
   {
      return name + " of " + suit;
   }
}