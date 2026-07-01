import java.util.*;

public class PokerPlayer
{
   private String name, type;
   private int startingStack, chips, currentBet, stackHandStart;
   private ArrayList<Card> hand;
   private boolean inHand, firstAct, allIn, bluffing;
   
   public PokerPlayer(String botName, int start)
   {
      name = botName;
      chips = start;
      startingStack = start;
      stackHandStart = startingStack;
      hand = new ArrayList<Card>();
      inHand = false;
      firstAct = true;
      allIn = false;
      bluffing = false;
      currentBet = 0;
      
      int random = (int)(Math.random() * 3);
      if(random == 0)
         type = "Expert";
      else if(random == 1)
         type = "Regular";
      else
         type = "Amateur";
   }
   
   public int getChips()
   {
      return chips;
   }
   
   public void setChips(int c)
   {
      chips = c;
   }
   
   public void bet(int amt)
   {
      Client.changePot(amt);
      currentBet += amt;
      chips -= amt;
   }
   
   public void anteBet(int amt)
   {
      Client.changePot(amt);
      chips -= amt;
   }
   
   public void resetBet()
   {
      currentBet = 0;
   }
   
   public int getCurrentBet()
   {
      return currentBet;
   }
   
   public String getName()
   {
      return name;
   }
   
   public boolean getInHand()
   {
      return inHand;
   }
   
   public void setInHand(boolean b)
   {
      inHand = b;
   }
   
   public boolean getFirstAct()
   {
      return firstAct;
   }
   
   public void setFirstAct(boolean b)
   {
      firstAct = b;
   }
   
   public boolean getAllIn()
   {
      return allIn;
   }
   
   public void setAllIn(boolean b)
   {
      allIn = b;
   }
   
   public boolean getBluffing()
   {
      return bluffing;
   }
   
   public void setBluffing(boolean b)
   {
      bluffing = b;
   }
   
   public ArrayList<Card> getHand()
   {
      return hand;
   }
   
   public String getCard(int index)
   {
      return hand.get(index).toString();
   }
   
   public int getCardValue(int index)
   {
      return hand.get(index).getValue();
   }
   
   public String getCardSuit(int index)
   {
      return hand.get(index).getSuit();
   }
   
   public void addToHand(Card c)
   {
      hand.add(c);
   }
   
   public Card removeFromHand()
   {
      return hand.remove(0);
   }
   
   public static Card findCard(int value, ArrayList<Card> rh)
   {
      for(Card card : rh)
      {
         if(card.getValue() == value)
         {
            return card;
         }
      }
      return new Card();
   }
   
   public Card findCard(String suit, ArrayList<Card> rh)
   {
      for(Card card : rh)
      {
         if(card.getSuit().equals(suit))
         {
            return card;
         }
      }
      return new Card();
   }
   
   public int getStackHandStart()
   {
      return stackHandStart;
   }
   
   public void setStackHandStart(int i)
   {
      stackHandStart = i;
   }
   
   public String getHandType()
   {
      // make rh, an array list of the player's hole cards and the river
      ArrayList<Card> rh = new ArrayList<Card>();
      if(Client.getRiver() != null)
      {
         for(int i = 0; i < Client.getRiver().size(); i++)
            rh.add(Client.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      else
      {
         for(int i = 0; i < AiTrainer.getRiver().size(); i++)
            rh.add(AiTrainer.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      
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
         else if(rank == 3)
            threesOfAKind++;
         if(rank == 2)
            pairs++;
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
         straightCards[0] = findCard(14, rh);
         straightCards[1] = findCard(2, rh);
         straightCards[2] = findCard(3, rh);
         straightCards[3] = findCard(4, rh);
         straightCards[4] = findCard(5, rh);
         
      }
      // other straights
      for(int i = 2; i < ranks.length - 2; i++)
      {
         if(ranks[i - 2] >= 1 && ranks[i - 1] >= 1 && ranks[i] >= 1 && ranks[i + 1] >= 1 && ranks[i + 2] >= 1)
         {
            straight = true;
            for(int j = 0; j < 5; j++)
               straightCards[j] = findCard(i + j, rh);
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
      String output;
      if(straightFlush && straightCards[4].getValue() == 14 && straightCards[3].getValue() == 13)
      {
         output = "Royal Flush";
      }
      else if(straightFlush)
      {
         if(wheel)
            output = "Straight Flush (The Wheel)";
         else   
            output = "Straight Flush (to the " + straightCards[4].getName() + ")";
      }
      else if(foursOfAKind >= 1)
      {
         output = "Four of a Kind (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 4)
            {
               output += findCard(i + 2, rh).getName() + "s)";
               break;
            }
         }
      }
      else if(pairs >= 1 && threesOfAKind >= 1)
      {
         output = "Full House (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 3)
            {
               output += findCard(i + 2, rh).getName() + "s full of ";
               break;
            }
         }
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 2)
            {
               output += findCard(i + 2, rh).getName() + "s)";
               break;
            }
         }
      }
      else if(suits[0] >= 5 || suits[1] >= 5 || suits[2] >= 5 || suits[3] >= 5)
      {
         output = "Flush (";
         String suit = "";
         if(suits[0] >= 5)
         {
            suit = "Hearts";
         }
         else if(suits[1] >= 5)
         {
            suit = "Diamonds";
         }
         else if(suits[2] >= 5)
         {
            suit = "Clubs";
         }
         else
         {
            suit = "Spades";
         }
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 1 && findCard(i + 2, rh).getSuit().equals(suit))
            {
               output += findCard(i + 2, rh).getName() + " high)";
               break;
            }
         }
         
      }
      else if(straight)
      {
         if(wheel)
            output = "Straight (The Wheel)";
         else
            output = "Straight (to the " + straightCards[4].getName() + ")";
      }
      else if(threesOfAKind >= 1)
      {
         output = "Three of a Kind (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 3)
            {
               output += findCard(i + 2, rh).getName() + "s)";
               break;
            }
         }
         
      }
      else if(pairs >= 2)
      {
         output = "Two Pair (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 2)
            {
               ranks[i] = 99;
               output += findCard(i + 2, rh).getName() + "s and ";
               break;
            }
         }
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 2)
            {
               output += findCard(i + 2, rh).getName() + "s)";
               break;
            }
         }
      }
      else if(pairs >= 1)
      {
         output = "Pair (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 2)
            {
               output += findCard(i + 2, rh).getName() + "s)";
               break;
            }
         }
      }
      else
      {
         output = "High Card (";
         for(int i = 12; i >= 0; i--)
         {
            if(ranks[i] == 1)
            {
               output += findCard(i + 2, rh).getName() + " high)";
               break;
            }
         }
      }
      return output;
   }
   
   public int getHandRank()
   {
      String handType = this.getHandType();
      if(handType.indexOf("Royal Flush") != -1)
         return 9;
      else if (handType.indexOf("Straight Flush") != -1)
         return 8;
      else if (handType.indexOf("Four of a Kind") != -1)
         return 7;
      else if (handType.indexOf("Full House") != -1)
         return 6;
      else if (handType.indexOf("Flush") != -1)
         return 5;
      else if (handType.indexOf("Straight") != -1)
         return 4;
      else if (handType.indexOf("Three of a Kind") != -1)
         return 3;
      else if (handType.indexOf("Two Pair") != -1)
         return 2;
      else if (handType.indexOf("Pair") != -1)
         return 1;
      else if (handType.indexOf("High Card") != -1)
         return 0;
      else
         return -1;
   }
   
   
   /*
   pre: this player
   post:
   -1 = not a pair
    0 = underpair
    1 = bottom pair
    2 = a pair that is in between the lowest and highest cards in the river
    3 = top pair
    4 = overpair
   */
   public int getPairType()
   {
      ArrayList<Card> rh = this.getScoringHand();
      Card pair = new Card();
      for(int j = 0; j < rh.size() - 1; j++)
      {
         if(rh.get(j).getValue() == rh.get(j + 1).getValue())
         {
            pair = rh.get(j); // this is one of the cards in the pair
            break;
         }
      }
      
      ArrayList<Card> river = Client.getRiver();
      int max = river.get(0).getValue();
      for(int k = 0; k < river.size(); k++) // sorts the river in descending order
      {
         int l = k + 1;
         int f = -1;
         for(; l < river.size(); l++)
         {
            if(river.get(l).getValue() > max)
            {
               max = river.get(l).getValue();
               f = l;
            }
         }
         if(f != -1)
         {
            Card temp = river.get(k);
            river.set(k, river.get(f));
            river.set(f, temp);
         }
      }
      
      String type = this.getHandType();
      if(!type.equals("Pair"))
         return -1;
      if(river.get(0).getValue() < pair.getValue()) // if the pair is higher than the highest card on the board (overpair)
         return 4;
      if(river.get(0).getValue() == pair.getValue()) // if the pair is equal to the highest card on the board (top pair)
         return 3;
      if(river.get(0).getValue() < pair.getValue() && river.get(river.size() - 1).getValue() > pair.getValue()) // if they have a pair in between top and bottom pair (middle pair)
         return 2;
      if(river.get(river.size() - 1).getValue() == pair.getValue()) // if the pair is equal to the lowest card on the board (bottom pair)
         return 1;
      if(river.get(river.size() - 1).getValue() > pair.getValue()) // if the pair is lower than the lowest card on the board (underpair)
         return 0;
      return -1;
   }
   
   public boolean onADraw()
   {
       // make rh, an array list of the player's hole cards and the river
      ArrayList<Card> rh = new ArrayList<Card>();
      if(Client.getRiver() != null)
      {
         for(int i = 0; i < Client.getRiver().size(); i++)
            rh.add(Client.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      else
      {
         for(int i = 0; i < AiTrainer.getRiver().size(); i++)
            rh.add(AiTrainer.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      
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
      
      boolean gutshot = false;
      // check for gutshots
      for(int i = 0; i < ranks.length - 4; i++)
      {
         int gutshotCounter = 0;
         for(int j = i; j < i + 5; j++)
         {
            if(ranks[j] > 0)
               gutshotCounter++;
         }
         if(gutshotCounter == 4)
         {
            gutshot = true;
            break;
         }
      }
      
      boolean flushDraw = false;
      // check for flush draw
      for(int i = 0; i < suits.length; i++)
      {
         if(suits[i] == 4)
         {
            flushDraw = true;
            break;
         }
      }
      return gutshot || flushDraw;
   }
   
   // pre: an array of cards with the river cards and this player's 2 hole cards
   // post: return the 5 scoring cards that make this player's poker hand
   public ArrayList<Card> getScoringHand()
   {
      // make an array with the river and hole cards
      ArrayList<Card> rh = new ArrayList<Card>();
      if(Client.getRiver() != null)
      {
         for(int i = 0; i < Client.getRiver().size(); i++)
            rh.add(Client.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      else
      {
         for(int i = 0; i < AiTrainer.getRiver().size(); i++)
            rh.add(AiTrainer.getRiver().get(i));
         rh.add(hand.get(0));
         rh.add(hand.get(1));
      }
      
      // if hand size is 5 or less, return
      if(rh.size() <= 5)
         return rh;
      
      
      int handRank = this.getHandRank();
      String handType = this.getHandType();
      String subType = "";
      if(handType.indexOf("(") != -1)
         subType = handType.substring(handType.indexOf("("));
      
      ArrayList<Card> straightCards = new ArrayList<Card>();
      // checking for a straight
      // the wheel (A, 2, 3, 4, 5) is a special and separate check
      if(handType.equals("Straight (The Wheel)") || handType.equals("Straight Flush (The Wheel)")) 
      {
         straightCards.add(findCard(14, rh));
         straightCards.add(findCard(2, rh));
         straightCards.add(findCard(3, rh));
         straightCards.add(findCard(4, rh));
         straightCards.add(findCard(5, rh));
         return straightCards;
         
      }
      // order the array from greatest to least
      for(int i = 0; i < rh.size(); i++)
      {
         Card largest = rh.get(i);
         int index = i;
         for(int j = i + 1; j < rh.size(); j++)
         {
            if(rh.get(j).getValue() > largest.getValue())
            {
               largest = rh.get(j);
               index = j;
            }
         }
         rh.set(index, rh.get(i));
         rh.set(i, largest);
      }
      
      // other straighty bits
      if(handRank == 4 || handRank == 8 || handRank == 9) // 4 = straight, 8 = straight flush, 9 = royal flush
      {
         // remove duplicate cards because those don't matter in a straight
         for(int c = 0; c < rh.size(); c++)
         {
            for(int t = c + 1; t < rh.size(); t++)
            {
               if(rh.get(c).getValue() == rh.get(t).getValue())
               {
                  rh.remove(t);
                  t--;
               }
            }
         }
         if(rh.size() == 5)
            return rh;
         
         for(int i = 0; i < rh.size() - 4; i++)
         {
            if(rh.get(i).getValue() == rh.get(i + 1).getValue() + 1 && rh.get(i + 1).getValue() == rh.get(i + 2).getValue() + 1) // makes sure hand isn't something like A, K, T, 9, 8, 7, 6
            {
               straightCards.add(rh.get(i));
               straightCards.add(rh.get(i + 1));
               straightCards.add(rh.get(i + 2));
               straightCards.add(rh.get(i + 3));
               straightCards.add(rh.get(i + 4));
               break;
            }
         }
         return straightCards;
      }
      
      // if we have a flush, figure out the suit and return highest cards of that suit
      if(handType.indexOf("Flush") != -1)
      {
         int[] suits = new int[4];
         for(Card card : rh)
         {
            if(card.getSuit().equals("Hearts"))
               suits[0]++;
            else if(card.getSuit().equals("Diamonds"))
               suits[1]++;
            else if(card.getSuit().equals("Clubs"))
               suits[2]++;
            else if(card.getSuit().equals("Spades"))
               suits[3]++;
         }
         String suit = "";
         if(suits[0] >= 5)
         {
            suit = "Hearts";
         }
         else if(suits[1] >= 5)
         {
            suit = "Diamonds";
         }
         else if(suits[2] >= 5)
         {
            suit = "Clubs";
         }
         else
         {
            suit = "Spades";
         }
         
         for(int i = 0; i < rh.size(); i++)
         {
            if(!rh.get(i).getSuit().equals(suit))
            {
               rh.remove(i);
               i--;
            }
         }
         while(rh.size() > 5)
         {
            rh.remove(rh.size() - 1);
         }
         return rh;
      }
      
      // for each hand, figure out which cards score if there are more than 5 in hand
      if(handRank == 7) // four of a kind (ex: A, K, J, 3, 3, 3, 3)
      {
         if(rh.get(1).getValue() != rh.get(2).getValue()) // -, -, 1, 1, 1, 1, -
            rh.remove(1);
         else if(rh.get(2).getValue() != rh.get(3).getValue()) // -, -, -, 1, 1, 1, 1
         {
            rh.remove(2);
            rh.remove(1);
         }
      }
      else if(handRank == 6) // full house (ex: A, T, T, T, 7, 5, 5)
      {
         while(rh.get(0).getValue() != rh.get(1).getValue()) // -, 1, 1, 1, 2, 2, - OR -, -, 1, 1, 1, 2, 2
            rh.remove(0);
         for(int i = 1; i < rh.size() - 2; i++)
         {
            if(rh.get(i).getValue() != rh.get(i + 1).getValue() && rh.get(i + 1).getValue() != rh.get(i + 2).getValue()) // 1, 1, 1, -, 2, 2, -
               rh.remove(i + 1);
         }
      }
      else if(handRank == 3) // three of a kind (ex: A, K, J, 10, 7, 7, 7)
      {
         /*if(rh.get(3).getValue() == rh.get(4).getValue() && rh.get(4).getValue() == rh.get(5).getValue()) // -, -, -, 1, 1, 1, -
            rh.remove(2);
         if(rh.get(4).getValue() == rh.get(5).getValue() && rh.get(5).getValue() == rh.get(6).getValue()) // -, -, -, -, 1, 1, 1
         {
            rh.remove(3);
            rh.remove(2);
         }*/
         ArrayList<Integer> threes = new ArrayList();
         for(int i = 0; i < rh.size() - 2; i++) // find the cards we don't want to remove
         {
            if(rh.get(i).getValue() == rh.get(i + 1).getValue() && rh.get(i + 1).getValue() == rh.get(i + 2).getValue())
            {
               threes.add(i);
               threes.add(i + 1);
               threes.add(i + 2);
               i++;
            }
         }
         for(int j = rh.size() - 1; j > 0; j--)
         {
            if(rh.size() <= 5)
               break;
            boolean found = false;
            for(int k = 0; k < threes.size(); k++)
            {
               if(threes.get(k) == j)
               {
                  found = true;
                  break;
               }
            }
            if(!found)
               rh.remove(j);
         }
      }
      else if(handRank == 2 || handRank == 1) // two pair or pair
      {
         ArrayList<Integer> pairs = new ArrayList();
         for(int i = 0; i < rh.size() - 1; i++) // find the cards we don't want to remove
         {
            if(rh.get(i).getValue() == rh.get(i + 1).getValue())
            {
               pairs.add(i);
               pairs.add(i + 1);
               i++;
            }
         }
         for(int j = rh.size() - 1; j > 0; j--)
         {
            if(rh.size() <= 5)
               break;
            boolean found = false;
            for(int k = 0; k < pairs.size(); k++)
            {
               if(pairs.get(k) == j)
               {
                  found = true;
                  break;
               }
            }
            if(!found)
               rh.remove(j);
         }
      }
      
      // remove cards until 5 remain in the hand, then return the scoring hand
      while(rh.size() > 5)
         rh.remove(rh.size() - 1);
      return rh;
   }
   
   public double getHandStrength() // !! thank you to Bill Chen for making this formula !!
   {
      // set an int for checking hand strength, make array of hole cards
      double strength = 0;
      Card[] hole = new Card[2];
      if(this.getCardValue(0) > this.getCardValue(1))
      {
         hole[0] = hand.get(0);
         hole[1] = hand.get(1);
      }
      else
      {
         hole[0] = hand.get(1);
         hole[1] = hand.get(0);
      }
      
      // add points based on the higher card in hand
      if(hole[0].getValue() == 14) // A
         strength += 10;
      if(hole[0].getValue() == 13) // K
         strength += 8;
      if(hole[0].getValue() == 12) // Q
         strength += 7;
      if(hole[0].getValue() == 11) // J
         strength += 6;
      else
         strength += hole[0].getValue() / 2;
                  
      // pocket pair doubles the score (55 is strength 6, minimum score for a pair is 5)
      if(hole[0].getValue() == hole[1].getValue())
      {
         strength *= 2;
         if(strength < 5)
            strength = 5;
         else if(hole[0].getValue() == 5)
            strength = 6;
      }
                  
      // if the cards are suited, add 2 points
      if(hole[0].getSuit().equals(hole[1].getSuit()))
         strength += 2;
                  
      // deducts points based on how big the gap is between the two cards
      int gap = hole[0].getValue() - hole[1].getValue() - 1;
      if(gap == 1)
         strength--;
      else if(gap == 2)
         strength -= 2;
      else if(gap == 3)
         strength -= 4;
      else if(gap > 3)
         strength -= 5;
                     
      // 1 bonus point if connected or 1-gap and highest card is lower than Q (makes offsuit and suited connectors and one-gappers have higher scores to reflect their strength)
      if((gap == 0 || gap == 1) && hole[0].getValue() < 12)
         strength++;
         
      return strength;
   }
}