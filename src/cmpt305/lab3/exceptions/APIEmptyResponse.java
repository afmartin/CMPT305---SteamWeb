package cmpt305.lab3.exceptions;

import java.io.IOException;

public class APIEmptyResponse extends IOException{
    public APIEmptyResponse(){
        super("API return no information");
    }
}
