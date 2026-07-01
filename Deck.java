import java.util.*;

public class Deck
{
   private ArrayList<Card> deck;
   
   public Deck()
   {
      deck = new ArrayList<Card>();
      for(int s = 0; s < 4; s++)
      {
         for(int c = 0; c < 13; c++)
         {
            deck.add(new Card(c, s));
         }
      }
   }
   
   public void shuffle()
   {
      ArrayList<Card> topHalf = new ArrayList<Card>();
      ArrayList<Card> bottomHalf = new ArrayList<Card>();
      
      int cuts = (int)(Math.random() * 3) + 2;
      int cutPoint;
      
      // for each cut, cut the cards and shuffle them
      for(int i = 0; i < cuts; i++)
      {
         // cut
         cutPoint = (int)(Math.random() * 10) + 21;
         int j = 0;
         while(j <= cutPoint && deck.size() > 0) {
            topHalf.add(deck.remove(0));
            j++;
         }
         while(deck.size() > 0) {
            bottomHalf.add(deck.remove(0));
         }
         // shuffle
         while(topHalf.size() > 0 && bottomHalf.size() > 0) 
         {
            deck.add(topHalf.remove(0));
            deck.add(bottomHalf.remove(0));
         }
         if(bottomHalf.size() > 0) {
            while(bottomHalf.size() > 0)
            {
               deck.add(bottomHalf.remove(0));
            }
         } else {
            while(topHalf.size() > 0)
            {
               deck.add(topHalf.remove(0));
            }
         }
      }// end of one cut and shuffle
   }// end of shuffle method
   
   public Card deal()
   {
      return deck.remove(0);
   }
   
   public void restack(Card card)
   {
      deck.add(card);
   }
   
   public void add(int i, Card card)
   {
      deck.add(i, card);
   }
   
   @Override
   public String toString()
   {
      return deck.toString();
   }
}