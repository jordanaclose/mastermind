
// REFERENCES:
// Lahcen's "Mastermind.java"
// https://en.wikipedia.org/wiki/Mastermind_(board_game)
// http://math.stackexchange.com/questions/1192961/knuths-mastermind-algorithm

// This cleverer Mastermind programme implements "Knuth's 5 Guess Algorithm" (but I didn't get round to implementing the minimax technique so
// my programme takes a max of 6 guesses rather than 5)

import java.util.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CleverMastermind extends JFrame{

	//declares integer variables for the width and height (number of pegs across and downwards), the number of colours to choose from and the number of guesses the computer has taken
	static int width;
	static int height;
	static int numColors;
	static int numGuesses = 0;

	static int wthings;
	static int bthings;

	static String blackpegs; //strings to use in methods below
	static String whitepegs;
	static String pegs;

	//declares arrays of buttons for the coloured pegs, white pegs and black pegs
	JButton[][] colouredPegs;
	JButton[][] whites;
	JButton[][] blacks;

	static int[] userCode; //declares an array of ints which represent the user's chosen code
	static JButton[][] userButtons; //declares an array of buttons which represent the user's chosen code
	static int[] computerGuess; //declares an array of ints to hold the computer's guesses
	int[] correctCode; //declares an array of ints to hold the correct code when found
	int state[][]; //declares a 2D array which represents the state(colour) of the pegs

	JButton confirm = new JButton("Confirm"); //initialises new button with the text "Confirm" on it
	JButton next = new JButton("Next"); //initialises new button with the text "Next" on it

	static ArrayList<String> S = new ArrayList<String>(); //arraylist to hold set of all possible combinations
	static ArrayList<Integer> remove = new ArrayList<Integer>();

	JPanel userPanel = new JPanel(); //initialises a panel to hold the user's code
	JPanel colouredPanel = new JPanel(); //initialises a panel to hold the coloured buttons
	JPanel whitesPanel = new JPanel(); //initialises a panel to hold the white buttons
	JPanel blacksPanel = new JPanel(); //initialises a panel to hold the black buttons
    JPanel panel2 = new JPanel(); //initialises a panel to hold all the other panels


	//method to check how many black pegs need to be shown
		static int blacks (int [] one, int [ ] two) //takes the user's guessed code and the computer's code as parameters
		{
			int val=0;
			for (int i=0;i<one.length;i++) //loops through the code that the user has guessed
			{
			  if (one[i]==two[i]) val++; //if a peg that the user has guessed is the same colour as, and in the same place as, the computer's peg, increment val

			}
	        return val; //return the number of black pegs to be shown
	}

		public static String blackpegs(int a){ //method to show how many black pegs have been returned as a string (to be used for the algorithm)
			int b = a;
			blackpegs = "";
			for(int i = 0; i < b; i++){
				blackpegs = blackpegs + "B";
			}
			return blackpegs;
		}

		//method to check how many white pegs need to be shown
		static int whites (int [] one, int [ ] two) //takes the user's guessed code and the computer's code as parameters
		{
			boolean found;
			int [ ] oneA = new int[one.length]; //creates two new arrays of ints that are the same length as the user's guessed code
			int [ ] twoA = new int[one.length];
			for (int i=0;i<one.length;i++) //loops through the code that the user has guessed
			{
				oneA[i]=one[i]; //adds the ith element of the user's array to new array
				twoA[i]=two[i]; //adds the ith element of the computer's array to new array
			}
			int val=0;
			for (int i=0;i<one.length;i++)
			if (oneA[i]==twoA[i]) {oneA[i]=0-i-10;twoA[i]=0-i-20;}

			for (int i=0;i<one.length;i++)
			{ found=false;
			  for (int j=0;j<one.length && !found;j++)
			  {
			   if (i!=j && oneA[i]==twoA[j])
			   {val++;oneA[i]=0-i-10;twoA[j]=0-j-20;found=true;}
			  }

		    }
			return val;
		}

		public static String whitepegs(int a){ //method to show how many white pegs have been returned as a string (to be used for the algorithm)
			int b = a;
			whitepegs = "";
			for(int i = 0; i < b; i++){
				whitepegs = whitepegs + "W";
			}
			return whitepegs;
		}

		public static String bothpegs(String a, String b){ //combines the two strings to form something like "BBWW" if two black and two white pegs are shown
			blackpegs = a;
			whitepegs = b;
			return blackpegs + whitepegs;
		}

		public static int[] setUserCode(){ //method to set the userCode depending on what colours the user chose
			for(int i = 0; i < width; i++){
				if(userButtons[0][i].getBackground()==Color.red){
					userCode[i] = 0;
				}
				else if(userButtons[0][i].getBackground()==Color.green){
					userCode[i] = 1;
				}
				else if(userButtons[0][i].getBackground()==Color.orange){
					userCode[i] = 2;
				}
				else if(userButtons[0][i].getBackground()==Color.cyan){
					userCode[i] = 3;
				}
				else if(userButtons[0][i].getBackground()==Color.magenta){
					userCode[i] = 4;
				}
				else{
					userCode[i] = 5;
				}
			}
			return userCode;
		}



    //method to link an int with a particular colour of button
	static Color choose(int i){
		if (i==0) return Color.red;
		if (i==1) return Color.green;
		if (i==2) return Color.orange;
		if (i==3) return Color.cyan;
		if (i==4) return Color.magenta;
		else return Color.yellow;
	}

	public CleverMastermind(int h, int w, int c) {

		JOptionPane.showMessageDialog(null, "Please choose your code and then click 'Confirm'. Then, click 'Next' for the computer to guess your code.");

		//the parameters of the constructor decide the height, width and number of colours available
		width=w;
		height=h;
		numColors=c;

		//initialising arrays of ints and buttons
		userCode = new int[width];
		computerGuess = new int[width];
		correctCode = new int[width];
		state = new int[height][width];
		userButtons = new JButton[1][width];

		whites = new JButton[height][width];
		blacks = new JButton[height][width];
		colouredPegs = new JButton[height][width];

		colouredPanel.setLayout(new GridLayout(height,width)); //lays the components out in a rectangular grid
		blacksPanel.setLayout(new GridLayout(height,width)); //lays the components out in a rectangular grid
		whitesPanel.setLayout(new GridLayout(height,width)); //lays the components out in a rectangular grid
		colouredPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //sets an empty border with an inset of 20 so that the panels aren't right next to eachother
		whitesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //sets an empty border with an inset of 20 so that the panels aren't right next to eachother
		blacksPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //sets an empty border with an inset of 20 so that the panels aren't right next to eachother
		userPanel.setLayout(new GridLayout(1,width)); //lays the components out in a rectangular grid
		userPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //sets an empty border with an inset of 20 so that the panels aren't right next to eachother

		String guess;
		for(int j = 0; j <= 5555; j++){
			guess = String.format("%04d", j); //format string to prepend zeros if needed
			if(!guess.contains("6") && !guess.contains("7") && !guess.contains("8") && !guess.contains("9")){ //only include the arrangements of numbers that include 0,1,2,3,4 and 5
				S.add(guess); //add every possible combination to a set S
			}
		}

		class bing implements ActionListener{ //actionlistener to choose the colours of the pegs
			int x; int y;

			public void actionPerformed(ActionEvent e) {
				state[x][y]=(state[x][y]+1)%numColors; //when the button is clicked, the colour of the peg advances by 1
				((JButton)(e.getSource())).setBackground(choose(state[x][y])); //sets the background colour of the peg depending on how many times the user clicked
			}

			public bing (int p,int q){
				x=p;y=q;
			}
		}

		class confirm implements ActionListener{ //action listener for confirm button
			public void actionPerformed(ActionEvent e) {
				setUserCode();
				confirm.setVisible(false); //hides the "confirm" button and replaces it with the "next" button
				next.setVisible(true);
				for(int j = 0; j < 4; j++){
					userButtons[0][j].setEnabled(false); //disable buttons for the user's code once they've confirmed their code
				}
			}
		}

		class Pegs implements ActionListener{ //action listener to show black and white pegs every time computer makes a guess
			public void actionPerformed(ActionEvent e) {
				computerGuess();
				wthings = whites(computerGuess,setUserCode()); // sets "wthings" to the int returned by the whites() method
				bthings = blacks(computerGuess,setUserCode()); // sets "bthings" to the int returned by the blacks() method
				for(int j = 0; j < width; j++){
					colouredPegs[numGuesses][j].setBackground(choose(computerGuess[j])); //sets the next row of coloured pegs to whatever the computer's guess is
				}
				for (int i=0;i<width;i++)colouredPegs[numGuesses][i].setEnabled(false); //stops the row of coloured buttons from being able to be clicked on
				if (numGuesses< height)
				{
				  for (int i=0;i<wthings;i++) whites[numGuesses][i].setVisible(true); //if the max number of guesses hasn't been reached yet, display the relevent white pegs
				  for (int i=0;i<bthings;i++)blacks[numGuesses][i].setVisible(true); //if the max number of guesses hasn't been reached yet, display the relevent black pegs
				  numGuesses++; //increment numGuesses every time a guess is made
				  if (numGuesses< height) for (int i=0;i<width;i++) colouredPegs[numGuesses][i].setVisible(true); //display the coloured pegs up to the current row that the user is on
				}
			}
		}

		for (int k = 0; k < width; k++)
		{
			userButtons [0][k] = new JButton();
			userButtons [0][k].addActionListener(new bing(1,k)); //gives the array of coloured pegs an actionlistener to respond to being clicked
			userButtons [0][k].setBackground(choose(state[0][k])); //based on how many times the user clicks, the background colour of the pegs changes
			userPanel.add(userButtons [0][k]);
		}

		for (int i = 0; i < height; i++)
		for (int j = 0; j < width; j++)
		{
			//System.out.println(i +" "+j);
			state[i][j]=0; //initially sets all buttons to be red
			colouredPegs [i][j] = new JButton();

			//initialise the entire arrays of white and black pegs and sets the colours of the backgrounds, but makes the pegs invisible to the user until later
			whites [i][j]= new JButton();
			whites [i][j].setVisible(false);
			whites [i][j].setBackground(Color.white);
			blacks [i][j]= new JButton();
			blacks [i][j].setVisible(false);
			blacks [i][j].setBackground(Color.black);

			//adds the pegs (buttons) to their respective panels
			colouredPanel.add(colouredPegs [i][j]);
			whitesPanel.add(whites [i][j]);
			blacksPanel.add(blacks [i][j]);
			if (i>0) {
			colouredPegs[i][j].setVisible(false); //hides all of the buttons below the row that the user is currently on
			}
		}

		setLayout(new BorderLayout());
		add(blacksPanel, "West"); //puts the panel of black pegs on the left side of the screen
		add(colouredPanel, "Center"); //puts the panel of coloured pegs in the centre of the screen
		add(whitesPanel, "East"); //puts the panel of white pegs on the right side of the screen

		//create "confirmpanel" to hold the confirm button
		JPanel confirmPanel = new JPanel();
		confirmPanel.setLayout(new FlowLayout());
		confirmPanel.add(confirm); //add the confirm button
		confirmPanel.add(next);
		next.setVisible(false);
		add(confirmPanel,"South"); //add the panel to the bottom of the window

		//create "toppanel" to hold all of the headings for the other panels and the computer's hidden code
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		topPanel.setLayout(new GridLayout(1,3));
		topPanel.add(new JLabel("Blacks",JLabel.CENTER)); //add a label for the black pegs
		topPanel.add(userPanel); //add the user's chosen code
		topPanel.add(new JLabel("Whites",JLabel.CENTER)); //add a label for the white pegs
		add(topPanel,"North"); //add the panel to the top of the window

		setDefaultCloseOperation(3); //hide and dispose of the window when the user closes it (I think)
		setTitle("Clever Mastermind"); //sets title to Naive Mastermind
		setMinimumSize(new Dimension(width*50,height*50)); //sets the minimum size allowed for the window
		pack();
		setVisible(true); //makes it visible to the user
		confirm.addActionListener(new confirm()); //adds an actionlistener to the confirm button
		next.addActionListener(new Pegs()); //adds an actionlistener to the next button
		}


		//method to guess the code using Donald Knuth's Five Guess algorithm
		public static int[] computerGuess(){
			int numRemoves = 0;
			int num = 0;
			int[] temp = new int[width];

			if(numGuesses == 0){ //for the first guess, try "0011"
				for(int k = 0; k < 2; k++){
					computerGuess[k] = 0;
				}
				for(int k = 2; k < 4; k++){
					computerGuess[k] = 1;
				}
				return computerGuess;
			}
			else{
				for(int n = 0; n < S.size(); n++){
					for(int m = 0; m < width; m++){
						String s = Character.toString(S.get(n).charAt(m)); //convert each code in S to an array of ints called temp
						temp[m] = Integer.parseInt(s);
					}
					int bb = blacks(computerGuess, setUserCode()); //apply the blacks and whites methods to the computer guess and usercode to work out how many black and white pegs were returned
					int ww = whites(computerGuess, setUserCode());
					String bill = bothpegs(blackpegs(bb),whitepegs(ww)); //convert this into a string (e.g. BBWW)

					int b = blacks(computerGuess, temp); //apply the blacks and whites methods to each code in S to work out how many blacks and whites they would return
					int w = whites(computerGuess, temp);
					String bob = bothpegs(blackpegs(b),whitepegs(w)); //convert this to a string (e.g. BBWW)

					if(!(bill.equals(bob))){ //if they're not equal, remove from S
						remove.add(num,n); //add the position of the code to be removed to an arraylist
						numRemoves++; //count the number of removals that have been made
						num++;
					}
					if(bb == 4){ //if the number of black pegs is 4, the game has been won
						JOptionPane.showMessageDialog(null, "The computer guessed your code in " + numGuesses + " guesses.");
						System.exit(0);
					}
				}
				for(int d = numRemoves-1; d >= 0; d--){
					int e = remove.get(d);
					S.remove(e); //remove from S every code that isn't equal (as worked out a few lines earlier)
				}
				for(int x = 0; x < width; x++){
					computerGuess[x] = Integer.parseInt(Character.toString(S.get(0).charAt(x))); //choose the first code from S (which now has wrong codes removed) and convert back to an array of ints to be guessed next
				}
				remove.clear(); //clear the arraylist of removals so that it can be filled up again next turn
				return computerGuess;
			}
		}

		public static void main(String[] args) {
			new CleverMastermind(6,4,6);
		}

}