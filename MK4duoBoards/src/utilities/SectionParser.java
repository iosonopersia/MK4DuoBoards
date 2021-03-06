package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import model.Board;
import model.Microcontroller;
import persistence.ConfigPersister;

public class SectionParser {
	/****************************************************************************************
	 * As for now, we check that the section is finished
	 * verifying that TOKEN.startsWith(Const.MK4DUOBOARDS_SECTION_END).
	 * 
	 * This is a strict requirement for the file format, in fact
	 * Const.MK4DUOBOARDS_SECTION_END must be at the start of a new line!!!
	 * 
	 * To loosen this requirement we may replace the use of startsWith()
	 * with contains(). By doing this, a line which contains Const.MK4DUOBOARDS_SECTION_END
	 * will be ignored, even if it carries useful information.
	 * 
	 * FOR THIS REASON, IT'S A GOOD IDEA LEAVING THE CODE AS IT IS,
	 * AT LEAST FOR A WHILE!!!
	 ****************************************************************************************/
	
	public static void parseMK4DuoSection(String sectionName, BufferedReader br, Board currentBoard) throws IOException {
		if(sectionName.equals(Const.MK4DUOBOARDS_BOARD_NAME_SECTION)){
			parseBoardNameSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_CHIP_SECTION)){
			parseChipSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_SERVOMOTORS_SECTION)){
			parseServoMotorsSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_UNKNOWN_PINS_SECTION)){ 
			parseUnknownPinsSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_IF_BLOCKS_SECTION)){
			parseIfBlocksSection(br, currentBoard);
		}else if(sectionName.equals(Const.MK4DUOBOARDS_MB_SETUP_SECTION)) {
			parseMBSetupSection(br, currentBoard);
		}
		
	}
	
	
	private static void parseBoardNameSection(BufferedReader br, Board currentBoard) throws IOException {
		String line,
		   defineToken= Const.EMPTY,
		   boardNameToken= Const.EMPTY,
		   boardNameString= Const.EMPTY;
		
		while((line= br.readLine())!=null && defineToken.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			StringTokenizer st= new StringTokenizer(line);
			defineToken= SecureTokenizer.readToken(st);
			boardNameToken=SecureTokenizer.readToken(st);
		
			if(defineToken.equals(Const.DEFINE) && boardNameToken.equals(Const.BOARD_NAME_TOKEN)){
				//We've found the BOARD_NAME token!
				SecureTokenizer.readToken(st, Const.QUOTE);
				boardNameString= SecureTokenizer.readToken(st, Const.QUOTE);
				currentBoard.setName(boardNameString.trim());
			}
		}
	}
	
	private static void parseChipSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder parsedCheckCode= new StringBuilder();
		String line= Const.EMPTY;
		
		while((line=br.readLine()) != null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			if(line.isEmpty()==false){
				parsedCheckCode.append(line);
				parsedCheckCode.append(Const.EOL);
				
			}
		}
		for(String chipName: ConfigPersister.getChipNames()){
			Microcontroller chip= ConfigPersister.getChip(chipName);
			if(chip.getCheckCode().equals(parsedCheckCode.toString())){
				currentBoard.setMicrocontroller(chip);
				break;
			}
		}
	}
	
	private static void parseServoMotorsSection(BufferedReader br, Board currentBoard) throws IOException {
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			StringTokenizer st= new StringTokenizer(line);
			String defineToken= SecureTokenizer.readToken(st);
			String pinNameToken= SecureTokenizer.readToken(st);
			String pinValueToken= SecureTokenizer.readToken(st);
			String pinComment= SecureTokenizer.readInlineCommentToken(st);
			
			if(defineToken.equals(Const.DEFINE)){
				String section= ConfigPersister.getSectionNameOf(pinNameToken);
				if(section != null){
					//When we find the first occurrence, we stop.
					//Well,it should exist only 1 occurrence for every pin...
					currentBoard.setPinValueByNameAndSection(pinNameToken, section, pinValueToken);
					currentBoard.getPinByNameAndSection(pinNameToken, section).setComment(pinComment);
					
				}
			}
		}
		
	}
	
	private static void parseUnknownPinsSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder sb= new StringBuilder();
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			sb.append(line);
			sb.append(Const.EOL);
		}
		currentBoard.setUnknownPins(sb.toString());
	}
	
	private static void parseIfBlocksSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder sb= new StringBuilder();
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			sb.append(line);
			sb.append(Const.EOL);
		}
		currentBoard.setIfBlocks(sb.toString());
	}

	private static void parseMBSetupSection(BufferedReader br, Board currentBoard) throws IOException {
		StringBuilder sb= new StringBuilder();
		String line;
		while((line=br.readLine())!=null && line.startsWith(Const.MK4DUOBOARDS_SECTION_END)==false){
			sb.append(line);
			sb.append(Const.EOL);
		}
		currentBoard.setMBSetup(sb.toString());
	}
	
//	private static void ignoreSection(BufferedReader br) throws IOException {
//		String line;
//		while((line= br.readLine())!=null){
//			if(line.startsWith(Const.MK4DUOBOARDS_SECTION_END)){
//				return;
//			}
//		}
//	}
}
