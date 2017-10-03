import java.io.File;

public class Sequence_Parser {

	public static void main(String[] args) throws Exception {
		
		Sequence_Parser parser = new Sequence_Parser();
		
		//Check for input file
		
		if(args.length == 2){
			File inputFolder = new File(args[0]);
			String output = args[1];
			
			if(inputFolder.isDirectory()){
				Seq seq = new Seq();
				seq.execute(inputFolder,output);
				
			}
			
			else{
				throw new Exception("Please enter a valid input folder");
			}
		}
		
		else{
			throw new Exception("Please enter input folder path and output file name");
			
		}
		
		
	}
}
