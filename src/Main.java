import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws IOException {

        ArrayList<Square> square = new ArrayList<>(); /* It keeps all boxes in the Square */
        ArrayList<String> chanceCards = new ArrayList<>(); /* It keeps the content of the chance cards as a String */
        ArrayList<String> communityCards = new ArrayList<>(); /* It keeps the content of the community chest cards as a String */
        User player1 = new Player("Player 1", 15000);
        User player2 = new Player("Player 2", 15000);
        User banker = new Banker("Banker", 100000);
        Square go = new Square(1, "GO"); /* The starting box */
        square.add(go);


        JSONParser parser = new JSONParser();

        /* Reading the JSON files ( list.json and property.json ) */
        try {
            Object objForProp = parser.parse(new FileReader("property.json"));
            Object objForCard = parser.parse(new FileReader("list.json"));

            JSONObject jsonObjectProp = (JSONObject) objForProp;
            JSONArray one = (JSONArray) jsonObjectProp.get("1"); /* For Land */
            JSONArray two = (JSONArray) jsonObjectProp.get("2"); /* For Company */
            JSONArray three = (JSONArray) jsonObjectProp.get("3"); /* For Rail Roads */

            for (Object o1 : one) { /* Land part */

                JSONObject propertyObj = (JSONObject) o1;

                String name = (String) propertyObj.get("name");
                int id = Integer.parseInt((String) propertyObj.get("id"));
                int cost = Integer.parseInt((String) propertyObj.get("cost"));

                Square newLand = new Land(id, name, cost);
                square.add(newLand);
            }

            for (Object o2 : two) { /* Company part */

                JSONObject propertyObj = (JSONObject) o2;

                String name = (String) propertyObj.get("name");
                int id = Integer.parseInt((String) propertyObj.get("id"));
                int cost = Integer.parseInt((String) propertyObj.get("cost"));

                Square newCompany = new Company(id, name, cost);
                square.add(newCompany);
            }

            for (Object o3 : three) { /* Railroads part */

                JSONObject propertyObj = (JSONObject) o3;

                String name = (String) propertyObj.get("name");
                int id = Integer.parseInt((String) propertyObj.get("id"));
                int cost = Integer.parseInt((String) propertyObj.get("cost"));

                Square newRailRoad = new RailRoads(id, name, cost);
                square.add(newRailRoad);
            }

            JSONObject jsonObjectCard = (JSONObject) objForCard;
            JSONArray chance = (JSONArray) jsonObjectCard.get("chanceList");
            JSONArray community = (JSONArray) jsonObjectCard.get("communityChestList");

            for (Object o4 : chance) { /* Chance cards part */

                JSONObject cardObj = (JSONObject) o4;

                String item = (String) cardObj.get("item");
                chanceCards.add(item);
            }

            for (Object o5 : community) { /* Community Cards part */

                JSONObject cardObj = (JSONObject) o5;

                String item = (String) cardObj.get("item");
                communityCards.add(item);
            }

            int[] idsOfChanceCards = {8, 23, 37};
            int[] idsOfCommunityCards = {3, 18, 34};

            for (int i = 0; i < 3; i++) {

                Square newChance = new Chance(idsOfChanceCards[i], "Chance");
                Square newCommunity = new CommunityChest(idsOfCommunityCards[i], "Community Chest");
                square.add(newChance);
                square.add(newCommunity);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

            Square newSuperTax = new Tax(39, "Super Tax");
            Square newIncomeTax = new Tax(5, "Income Tax");
            Square newJail = new Jail(11, "Jail");
            Square newGoToJail = new Jail(31, "Go To Jail");
            Square newFreeParking = new FreeParking(21, "Free Parking");

            square.add(newSuperTax);
            square.add(newIncomeTax);
            square.add(newJail);
            square.add(newGoToJail);
            square.add(newFreeParking);

            Collections.sort(square, new Sorter()); /* It sorts the Square by id */


            Scanner command = new Scanner(new FileReader(args[0])); /* Command file */
            FileWriter output = new FileWriter("output.txt"); /* Output file */

            /* Reading the command file */
            while (command.hasNextLine()) {

                String move = command.nextLine();

                if (move.equals("show()")) {
                    show(player1,player2,banker,output);
                }
                else {
                    String player = move.split(";")[0];
                    int dice = Integer.parseInt(move.split(";")[1]);

                    if (player.equals(player1.getName())) { /* If the player is Player 1 */
                        ((Player) player1).setPosition(dice);
                        if (((Player) player1).getPosition() > 40) { /* If player passes the Go square, player gets 200 from banker */
                            ((Player) player1).setPosition(-40);
                            player1.setMoney(200);
                            banker.setMoney(-200);
                        }
                        Square position = square.get(((Player) player1).getPosition() - 1);

                        if (position instanceof Property) {
                            processesOfProperty(position, dice, player1, player2, banker, player1, player2,output);
                        }
                        else if (position instanceof Card) {
                            processesOfCard(square, position, chanceCards, communityCards, player1, player2, banker, dice, player1, player2,output);
                        }
                        else if (position instanceof Tax) {
                            player1.setMoney(-100); /* Player pays the tax */
                            banker.setMoney(100);
                            output.write(player1.getName() + "\t" + dice + "\t" + ((Player) player1).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player1.getName() + " paid Tax"+"\n");
                        }
                        else if (position instanceof Jail) { /* If player goes to the Jaiil */
                            if (position.getName().equals("Jail")) {
                                output.write(player1.getName() + "\t" + dice + "\t" + ((Player) player1).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player1.getName() + " went to jail"+"\n");
                                ((Player) player1).setCount();
                            }
                            else if (position.getName().equals("Go To Jail")) {
                                ((Player) player1).setPosition(-20);
                                output.write(player1.getName() + "\t" + dice + "\t" + ((Player) player1).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player1.getName() + " went to jail"+"\n");
                                ((Player) player1).setCount();

                            }
                        }
                        else if (position instanceof FreeParking) {
                            output.write(player1.getName() + "\t" + dice + "\t" + ((Player) player1).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player1.getName() + " is in Free Parking"+"\n");

                        }
                        else if (position.getName().equals("GO")) {
                            output.write(player1.getName() + "\t" + dice + "\t" + ((Player) player1).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player1.getName() + " is in GO square"+"\n");
                        }
                    }
                    else { /* If the player is Player 2 */
                        ((Player) player2).setPosition(dice);
                        if (((Player) player2).getPosition() > 40) { /* If player passes the Go square, player gets 200 from banker */
                            ((Player) player2).setPosition(-40);
                            player2.setMoney(200);
                            banker.setMoney(-200);
                        }
                        Square position = square.get(((Player) player2).getPosition() - 1);

                        if (position instanceof Property) {
                            processesOfProperty(position, dice, player1, player2, banker, player2, player1,output);
                        }
                        else if (position instanceof Card) {
                            processesOfCard(square, position, chanceCards, communityCards, player1, player2, banker, dice, player2, player1,output);
                        }
                        else if (position instanceof Tax) {
                            player2.setMoney(-100); /* Player pays the tax */
                            banker.setMoney(100);
                            output.write(player2.getName() + "\t" + dice + "\t" + ((Player) player2).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player2.getName() + " paid Tax"+"\n");
                        }
                        else if (position instanceof Jail) {
                            if (position.getName().equals("Jail")) {
                                output.write(player2.getName() + "\t" + dice + "\t" + ((Player) player2).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player2.getName() + " went to jail"+"\n");
                                ((Player) player2).setCount();
                            }
                            else if (position.getName().equals("Go To Jail")) {
                                ((Player) player2).setPosition(-20);
                                output.write(player2.getName() + "\t" + dice + "\t" + ((Player) player2).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player2.getName() + " went to jail"+"\n");
                                ((Player) player2).setCount();
                            }
                        }
                        else if (position instanceof FreeParking) {
                            output.write(player2.getName() + "\t" + dice + "\t" + ((Player) player2).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player2.getName() + " is in Free Parking"+"\n");
                        }
                        else if (position.getName().equals("GO")) {
                            output.write(player2.getName() + "\t" + dice + "\t" + ((Player) player2).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + player2.getName() + " is in GO square"+"\n");
                        }
                    }
                }
            }

            show(player1,player2,banker,output);

            command.close();
            output.close();

        }

        public static void processesOfProperty (Square position,int dice, User player1, User player2, User banker, User realPlayer, User otherPlayer, FileWriter output) throws IOException {

            if (((Player) realPlayer).getCount() > 0 && ((Player) realPlayer).getCount() < 4) { /* If player is in jail, it count until 3 */
                ((Player) realPlayer).setPosition(-dice);
                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " in jail (count=" + ((Player) realPlayer).getCount() + ")"+"\n");
                ((Player) realPlayer).setCount();
            }
            else {
                if (((Property) position).getOwner() == null) { /* If the square has no owner, player must buy this property */
                    if (((Property) position).getCost() <= realPlayer.getMoney()) { /* If player has enough money, player will buy */
                        realPlayer.setMoney(-((Property) position).getCost());
                        banker.setMoney(((Property) position).getCost());
                        ((Property) position).setOwner(realPlayer.getName());
                        ((Player) realPlayer).setPlaces(position.getName());
                        if (position instanceof RailRoads) {
                            ((Player) realPlayer).setHavingRR();
                        }
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " bought " + position.getName()+"\n");
                    }
                    else { /* If player has not enough money, player goes bankrupt */
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" +
                                player2.getMoney() + "\t" + realPlayer.getName() + " goes bankrupt"+"\n");
                        show(player1,player2,banker,output);
                        output.close();
                        System.exit(0); /* When a player goes bankrupt, the game will over */
                    }
                }
                else { /* If the square has a owner */
                    if (realPlayer.getName().equals(((Property) position).getOwner())) { /* If the owner is current player */
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" +
                                player2.getMoney() + "\t" + realPlayer.getName() + " has " + position.getName()+"\n");
                    }
                    else { /* If the owner is other player, current player will pay rent the other player */
                        if (position instanceof Land) {
                            realPlayer.setMoney(-(int) ((Land) position).getRent());

                            otherPlayer.setMoney((int) ((Land) position).getRent());
                            output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " paid rent for " + position.getName()+"\n");
                        }
                        else if (position instanceof Company) {
                            ((Company) position).setRent(dice);
                            realPlayer.setMoney(-(int) ((Company) position).getRent());
                            otherPlayer.setMoney((int) ((Company) position).getRent());
                            output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " paid rent for " + position.getName()+"\n");

                        }
                        else if (position instanceof RailRoads) {
                            int number = ((Player)otherPlayer).getHavingRR();
                            ((RailRoads) position).setRent(number);
                            realPlayer.setMoney(-(int) ((RailRoads) position).getRent());
                            otherPlayer.setMoney((int) ((RailRoads) position).getRent());
                            output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " paid rent for " + position.getName()+"\n");

                        }
                    }
                }
            }
        }

        public static void processesOfCard (ArrayList square, Square position, ArrayList < String > chanceCards, ArrayList < String > communityCards, User player1, User player2, User banker, int dice, User realPlayer, User otherPlayer, FileWriter output) throws IOException {

            if (((Player) realPlayer).getCount() > 0 && ((Player) realPlayer).getCount() < 4) { /* If the current player is in jail, it will count until 3*/
                ((Player) realPlayer).setPosition(-dice);
                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " in jail (count=" + ((Player) realPlayer).getCount() + ")"+"\n");
                ((Player) realPlayer).setCount();
            }
            else {
                if (position instanceof Chance) { /* If the card is Chance Card */
                    String card = chanceCards.get(0);
                    changingDeck(chanceCards); /* The card will go under the deck */
                    if (card.equals("Advance to Go (Collect $200)")) {
                        ((Player) realPlayer).setPosition(-(((Player) realPlayer).getPosition() - 1));
                        realPlayer.setMoney(200);
                        banker.setMoney(-200);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Go (Collect $200)"+"\n");
                    }
                    else if (card.equals("Advance to Leicester Square")) {
                        if (((Player) realPlayer).getPosition() > 27) { /* If player goes on Go square */
                            realPlayer.setMoney(200);
                            banker.setMoney(-200);
                            ((Player) realPlayer).setPosition(-(((Player) realPlayer).getPosition() - 27));
                            position = (Land) square.get(26);

                            if (((Land) position).getOwner() == null) { /* If Leicester Square has no owner */
                                if (((Land) position).getCost() <= realPlayer.getMoney()) { /* If player has enough money */
                                    realPlayer.setMoney(-((Land) position).getCost());
                                    banker.setMoney(((Land) position).getCost());
                                    ((Property) square.get(26)).setOwner(realPlayer.getName());
                                    ((Player) realPlayer).setPlaces(position.getName());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " +  "Chance -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " bought Leicester Square"+"\n");
                                }
                                else{ /* If player has no enough money */
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " +  "Chance -Advance to Leicester Square ");
                                    output.write(realPlayer.getName()+" goes bankrupt"+"\n");
                                    show(player1,player2,banker,output);
                                    output.close();
                                    System.exit(0);

                                }
                            }
                            else { /* If Leicester Square has a owner */
                                if (realPlayer.getName().equals(((Property) square.get(26)).getOwner())) {
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " +  "Chance -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " has Leicester Square"+"\n");
                                }
                                else {
                                    realPlayer.setMoney(-(int)((Land) position).getRent());
                                    otherPlayer.setMoney((int)((Land) position).getRent());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " +  "Chance -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " paid rent for Leicester Square"+"\n");
                                }
                            }
                        }
                        else {
                            ((Player) realPlayer).setPosition(27 - ((Player) realPlayer).getPosition());
                            position = (Land) square.get(26);

                            if (((Land) position).getOwner() == null) {
                                if (((Land) position).getCost() < realPlayer.getMoney()) {
                                    realPlayer.setMoney(-((Land) position).getCost());
                                    banker.setMoney(((Land) position).getCost());
                                    ((Land) square.get(26)).setOwner(realPlayer.getName());
                                    ((Player) realPlayer).setPlaces(position.getName());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " bought Leicester Square"+"\n");
                                }
                                else{
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Leicester Square ");
                                    output.write(realPlayer.getName()+" goes bankrupt"+"\n");
                                    show(player1,player2,banker,output);
                                    output.close();
                                    System.exit(0);

                                }
                            } else {
                                if (realPlayer.getName().equals(((Property) square.get(26)).getOwner())) {
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " has Leicester Square"+"\n");
                                } else {
                                    realPlayer.setMoney(-(int) ((Land) position).getRent());
                                    otherPlayer.setMoney((int) ((Land) position).getRent());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Leicester Square ");
                                    output.write(realPlayer.getName() + " paid rent for Leicester Square"+"\n");
                                }
                            }

                        }
                    }
                    else if (card.equals("Go back 3 spaces")) {
                        ((Player) realPlayer).setPosition(-3);
                        Square newPositon = (Square) square.get(((Player) realPlayer).getPosition() - 1);
                        if (newPositon instanceof CommunityChest) {
                            String cardOfGoBack = communityCards.get(0);
                            changingDeck(communityCards);
                            if (cardOfGoBack.equals("Advance to Go (Collect $200)")) {
                                ((Player) realPlayer).setPosition(-(((Player) realPlayer).getPosition() - 1));
                                realPlayer.setMoney(200);
                                banker.setMoney(-200);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw Advance to Go (Collect $200)"+"\n");
                            }
                            else if (cardOfGoBack.equals("Bank error in your favor - collect $75")) {
                                realPlayer.setMoney(75);
                                banker.setMoney(-75);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Bank error in your favor - collect $75"+"\n");
                            }
                            else if (cardOfGoBack.equals("Doctor's fees - Pay $50")) {
                                realPlayer.setMoney(-50);
                                banker.setMoney(50);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Doctor's fees - Pay $50"+"\n");

                            }
                            else if (cardOfGoBack.equals("It is your birthday Collect $10 from each player")) {
                                realPlayer.setMoney(10);
                                otherPlayer.setMoney(-10);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -It is your birthday Collect $10 from each player"+"\n");
                            }
                            else if (cardOfGoBack.equals("Grand Opera Night - collect $50 from every player for opening night seats")) {
                                realPlayer.setMoney(50);
                                player2.setMoney(-50);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Grand Opera Night - collect $50 from every player for opening night seats"+"\n");
                            }
                            else if (cardOfGoBack.equals("Income Tax refund - collect $20")) {
                                realPlayer.setMoney(20);
                                banker.setMoney(-20);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Income Tax refund - collect $20"+"\n");
                            }
                            else if (cardOfGoBack.equals("Life Insurance Matures - collect $100")) {
                                realPlayer.setMoney(100);
                                banker.setMoney(-100);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Life Insurance Matures - collect $100"+"\n");
                            }
                            else if (cardOfGoBack.equals("Pay Hospital Fees of $100")) {
                                realPlayer.setMoney(-100);
                                banker.setMoney(100);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Pay Hospital Fees of $100"+"\n");
                            }
                            else if (cardOfGoBack.equals("Pay School Fees of $50")) {
                                realPlayer.setMoney(-50);
                                banker.setMoney(50);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -Pay School Fees of $50"+"\n");
                            }
                            else if (cardOfGoBack.equals("You inherit $100")) {
                                realPlayer.setMoney(100);
                                banker.setMoney(-100);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -You inherit $100"+"\n");
                            }
                            else if (cardOfGoBack.equals("From sale of stock you get $50")) {
                                realPlayer.setMoney(50);
                                banker.setMoney(-50);
                                output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                output.write(realPlayer.getName() + " draw -From sale of stock you get $50"+"\n");
                            }
                        }
                        else if (newPositon instanceof Tax) {
                            realPlayer.setMoney(-100);
                            banker.setMoney(100);
                            output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                            output.write(realPlayer.getName() + "paid Tax"+"\n");
                        }
                        else if (newPositon instanceof Land) {
                            if (((Land) newPositon).getOwner() == null) {
                                if (((Land) newPositon).getCost() <= realPlayer.getMoney()) {
                                    realPlayer.setMoney(-((Land) newPositon).getCost());
                                    banker.setMoney(((Land) newPositon).getCost());
                                    ((Land) square.get(((Player) realPlayer).getPosition() - 1)).setOwner(realPlayer.getName());
                                    ((Player) realPlayer).setPlaces(newPositon.getName());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                    output.write(realPlayer.getName() + " bought " + newPositon.getName()+"\n");
                                }
                                else {
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " goes bankrupt"+"\n");
                                    show(player1,player2,banker,output);
                                    output.close();
                                    System.exit(0);
                                }
                            }
                            else {
                                if (realPlayer.getName().equals(((Land) newPositon).getOwner())) {
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                    output.write(realPlayer.getName() + " has " + newPositon.getName()+"\n");
                                }
                                else {
                                    realPlayer.setMoney(-(int) ((Land) newPositon).getRent());
                                    otherPlayer.setMoney((int) ((Land) newPositon).getRent());
                                    output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw Go back 3 spaces ");
                                    output.write(realPlayer.getName() + " paid rent for " + newPositon.getName()+"\n");
                                }
                            }
                        }
                    }
                    else if (card.equals("Pay poor tax of $15")) {
                        realPlayer.setMoney(-15);
                        banker.setMoney(15);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Pay poor tax of $15"+"\n");
                    }
                    else if (card.equals("Your building loan matures - collect $150")) {
                        realPlayer.setMoney(150);
                        banker.setMoney(-150);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Your building loan matures - collect $150"+"\n");
                    }
                    else if (card.equals("You have won a crossword competition - collect $100 ")) {
                        realPlayer.setMoney(100);
                        banker.setMoney(-100);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -You have won a crossword competition - collect $100"+"\n");
                    }
                }
                else if (position instanceof CommunityChest) {
                    String card = communityCards.get(0);
                    changingDeck(communityCards);
                    if (card.equals("Advance to Go (Collect $200)")) {
                        ((Player) realPlayer).setPosition(-(((Player) realPlayer).getPosition() - 1));
                        realPlayer.setMoney(200);
                        banker.setMoney(-200);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Advance to Go (Collect $200)"+"\n");
                    }
                    else if (card.equals("Bank error in your favor - collect $75")) {
                        realPlayer.setMoney(75);
                        banker.setMoney(-75);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Bank error in your favor - collect $75"+"\n");
                    }
                    else if (card.equals("Doctor's fees - Pay $50")) {
                        realPlayer.setMoney(-50);
                        banker.setMoney(50);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Doctor's fees - Pay $50"+"\n");
                    }
                    else if (card.equals("It is your birthday Collect $10 from each player")) {
                        realPlayer.setMoney(10);
                        otherPlayer.setMoney(-10);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -It is your birthday Collect $10 from each player"+"\n");
                    }
                    else if (card.equals("Grand Opera Night - collect $50 from every player for opening night seats")) {
                        realPlayer.setMoney(50);
                        player2.setMoney(-50);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Grand Opera Night - collect $50 from every player for opening night seats"+"\n");
                    }
                    else if (card.equals("Income Tax refund - collect $20")) {
                        realPlayer.setMoney(20);
                        banker.setMoney(-20);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Income Tax refund - collect $20"+"\n");
                    }
                    else if (card.equals("Life Insurance Matures - collect $100")) {
                        realPlayer.setMoney(100);
                        banker.setMoney(-100);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Life Insurance Matures - collect $100"+"\n");
                    }
                    else if (card.equals("Pay Hospital Fees of $100")) {
                        realPlayer.setMoney(-100);
                        banker.setMoney(100);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Pay Hospital Fees of $100"+"\n");
                    }
                    else if (card.equals("Pay School Fees of $50")) {
                        realPlayer.setMoney(-50);
                        banker.setMoney(50);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -Pay School Fees of $50"+"\n");
                    }
                    else if (card.equals("You inherit $100")) {
                        realPlayer.setMoney(100);
                        banker.setMoney(-100);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -You inherit $100"+"\n");
                    }
                    else if (card.equals("From sale of stock you get $50")) {
                        realPlayer.setMoney(50);
                        banker.setMoney(-50);
                        output.write(realPlayer.getName() + "\t" + dice + "\t" + ((Player) realPlayer).getPosition() + "\t" + player1.getMoney() + "\t" + player2.getMoney() + "\t" + realPlayer.getName() + " draw " + position.getName() + " -From sale of stock you get $50"+"\n");
                    }
                }
            }
        }

        public static void changingDeck (ArrayList < String > cards) { /* This method gets the first card, then it puts the card under the deck */

            String temp = cards.get(0);
            int i;
            for (i = 0; i < cards.size() - 1; i++) {
                cards.set(i, cards.get(i + 1));
            }
            cards.set(i, temp);
        }

        public static void show(User player1 , User player2 , User banker , FileWriter output) throws IOException {

            output.write("-------------------------------------------------------------------------------------------------------------------------"+"\n");
            output.write(player1.getName() + "\t" + player1.getMoney() + "\t" + "have: ");

            for (String place : ((Player) player1).getPlaces()) { /* Printing the places that belongs to Player 1 */
                if (((Player) player1).getPlaces().indexOf(place) == ((Player) player1).getPlaces().size() - 1) {
                    output.write(place);
                } else {
                    output.write(place+", ");
                }
            }
            output.write("\n");
            output.write(player2.getName() + "\t" + player2.getMoney() + "\t" + "have: ");

            for (String place : ((Player) player2).getPlaces()) { /* Printing the places that belongs to Player 2 */
                if (((Player) player2).getPlaces().indexOf(place) == ((Player) player2).getPlaces().size() - 1) {
                    output.write(place);
                } else {
                    output.write(place + ", ");
                }
            }
            output.write("\n");
            output.write(banker.getName() + "\t" + banker.getMoney()+"\n"); /* Printing the informations of Banker */

            if (player1.getMoney() > player2.getMoney()) { /* Printing the winner */
                output.write("Winner" + "\t" + player1.getName()+"\n");
            }
            else if (player2.getMoney() > player1.getMoney()){
                output.write("Winner" + "\t" + player2.getName()+"\n");
            }
            else {
                output.write("Winner Scoreless"+"\n");
            }
            output.write("-------------------------------------------------------------------------------------------------------------------------"+"\n");
        }
}
