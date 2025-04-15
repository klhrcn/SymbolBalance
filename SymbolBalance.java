import java.util.Stack;
import java.io.*;

public class SymbolBalance implements SymbolBalanceInterface{
    //instance variables
    private String filename;
    private Stack<Character> s;


    //constructor to initialize stack
    public SymbolBalance(){
        s = new Stack<>();
    }
    
    //write method to take in a String representing the path to the file that should be checked
    @Override
    public void setFile(String filename){
        this.filename = filename;
    }


    @Override
    public BalanceError checkFile(){
        if(filename == null){
            throw new IllegalStateException("File name is not set");
        }
        try (FileReader f = new FileReader(filename)){
            int readCharacter;
            int line = 1;
            boolean strings = false;
            boolean comments = false;
            char prev = '\0';
            
            while ((readCharacter = f.read()) != -1){
                char current = (char) readCharacter;

                if(current == '\n'){
                    line++;
                }

                if(current == '"' && !comments){
                    strings = !strings;
                }

                if (!strings){
                    if(!comments && prev == '/' && current =='*'){
                        comments = true;
                        s.push('*');
                    }else if(comments&&prev =='*' && current == '/'){
                        comments = false;
                        if(!s.isEmpty() && s.peek() == '*'){
                            s.pop();
                        }else{
                            return new EmptyStackError(line);
                        }
                    }
                }

                if (strings || comments){
                    prev = current;
                    continue;
                }

                if (current =='{' || current =='(' || current == '['){
                    s.push(current);
                }
                else if(current =='}' || current == ')' || current ==']'){
                    if(s.isEmpty()){
                        return new EmptyStackError(line);
                    }
                    char popped = s.pop();
                    if(!isMatchingPair(popped, current)){
                        return new MismatchError(line, current, popped);
                    }
                }
                prev = current;

                
            }
            if(!s.isEmpty()){
                        return new NonEmptyStackError((char)s.peek(), s.size());
            }
            f.close();
            
        } catch (IOException e){
            System.out.println(e);
        }

        return null;
    }

    //helper method

    private boolean isMatchingPair(char open, char close){
        return (open =='{' && close =='}') ||(open =='(' && close ==')') ||(open =='[' && close ==']');
    }

}
