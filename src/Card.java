public class Card {
    private int number;
    private boolean faceUp;

    public Card(int number){
        this.number = number;
    }

    public void flipCard(){
        this.faceUp = !this.faceUp;
    }

    public String getNumber(){
        if(this.faceUp){
            return String.valueOf(this.number);
        }else{
            return "*";
        }
    }

    public int getValue(){
        return this.number;
    }

    public boolean isFaceUp(){
        return this.faceUp;
    }

    public boolean compareCard(Card anotherCard){
        if(this == anotherCard){
            return false;
        }
        return this.number == anotherCard.getValue();
    }
}
