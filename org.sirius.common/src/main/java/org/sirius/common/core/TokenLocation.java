package org.sirius.common.core;

public interface TokenLocation {

	/** The index of the first character of this token relative to the beginning of the line at which it occurs, 0..n-1
	 * 
	 * @return
	 */
	public int getCharPositionInLine();
	
	/** The line number on which the 1st character of this token was matched, line=1..n
	 * 
	 * @return
	 */
	public int getLine();
	/** The starting character index of the token This method is optional; return -1 if not implemented.
	 * 
	 * @return
	 */
	public int getStartIndex();
	/** The last character index of the token. This method is optional; return -1 if not implemented.
	 * 
	 * @return
	 */
	public int getStopIndex();
	
	/** Location, eg file path, to be printed in messages (no semantic meaning) */
	public String getSourceName();

}
