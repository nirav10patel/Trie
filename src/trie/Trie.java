package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) 
	{
		TrieNode root = new TrieNode (null,null,null);
		Indexes index = new Indexes(0,(short)0,(short)0);
		
		int wordIndex;
		String word;
		int wordSize = 0;
		short ending = 0;
		
		if (allWords.length == 0)
		{
			return null;
		}
		
		else
		{
			for (int i = 0; i < allWords.length; i++)
			{
				wordIndex = i;
				word = allWords[i];
				wordSize = word.length();
				ending = (short)(wordSize-1);
				index = new Indexes(wordIndex,(short)0,ending);
				
				if (i == 0)
				{
					TrieNode first = new TrieNode (index,null,null);
					root.firstChild = first;
				}
				
				else
				{
					siblingSearch(allWords,root,word,wordIndex,word);
				}
			}
			
			return root;
		}
	}
	
	private static void siblingSearch(String[] allWords, TrieNode root, String word, int wordIndex,String part)
	{
		TrieNode prev = null;
		boolean exists = false;
		int ending = -1;
		TrieNode ptr = root.firstChild;
		String prefixNew = "";
		
		short prefixStart;
		short prefixEnd;
		
		do
		{
			int prefixIndex = ptr.substr.wordIndex;
			if (root.substr != null)
			{
				prefixStart = (short)(ptr.substr.startIndex + root.substr.startIndex);
				prefixEnd = (short)(ptr.substr.endIndex);
			}
			else
			{
				prefixStart = ptr.substr.startIndex;
				prefixEnd = ptr.substr.endIndex;
			}
			String prefix = allWords[prefixIndex].substring((int)prefixStart,(int)prefixEnd+1);
			
			for(int i = 0;i < prefix.length() && i < part.length();i++)
			{
				if(prefix.charAt(i) == part.charAt(i))
				{
					prefixNew = prefixNew + part.charAt(i);
					ending++;
				}
				else
				{
					break;
				}
			}
			
			if(prefixNew.equals(prefix))
			{
				exists = true;
				part = part.substring(prefix.length(), part.length());
				siblingSearch(allWords,ptr,word,wordIndex,part);
				break;
			}
			
			else
			{
				
				if (prefixNew.length() > 0)
				{
					Indexes indexNew = new Indexes (prefixIndex,prefixStart,(short)(ending + ptr.substr.startIndex));
					TrieNode brother = new TrieNode (indexNew,null,null);
					Indexes indexWordNew = null;
					ptr.substr.startIndex = (short)((ending + ptr.substr.startIndex + 1));
					indexWordNew = new Indexes(wordIndex,ptr.substr.startIndex,(short)(word.length()-1));
					
					TrieNode sister = new TrieNode (indexWordNew,null,null);
					
					if(prev == null)
					{
						root.firstChild = brother;
					}
					
					else
					{
						prev.sibling = brother;
					}
					
					brother.sibling = ptr.sibling;
					brother.firstChild = ptr;
					ptr.sibling = sister;
					exists = true;
					break;
				}
				
				else
				{
					prev = ptr;
					if (ptr.sibling == null)
					{
						break;
					}
					else
					{
						ptr = ptr.sibling;
					}
				}
			}
		} while(true);
		
		if (exists)
		{
			return;
		}
		
		else
		{
			Indexes indexWordNew;
			if(root.substr!= null)
			{
				indexWordNew = new Indexes(wordIndex,(short)(ptr.substr.startIndex + root.substr.startIndex),(short)(word.length()-1));
			}
			else
			{
				indexWordNew = new Indexes(wordIndex,ptr.substr.startIndex,(short)(word.length()-1));
			}
			TrieNode brother = new TrieNode (indexWordNew,null,null);
			ptr.sibling = brother;
			return;
		}
		
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix)
	{
		ArrayList <TrieNode> wordList = new ArrayList<TrieNode>();
		if (root.firstChild == null)
		{
			return null;
		}
		else
		{
			wordSearch(allWords,wordList,root,prefix,prefix.length());
		}
		
		if (wordList.isEmpty())
		{
			return null;
		}
		return wordList;
	}
	
	private static void wordSearch(String[] allWords, ArrayList <TrieNode> wordList, TrieNode root, String part, int partLength)
	{
		TrieNode ptr = root.firstChild;
		short prefixStart;
		short prefixEnd;
		String prefixNew = "";
		int placeLength = partLength;
		
		do
		{
			if (ptr == null)
			{
				return;
			}
			int prefixIndex = ptr.substr.wordIndex;
			
			prefixStart = ptr.substr.startIndex;
			prefixEnd = ptr.substr.endIndex;
			
			String prefix = allWords[prefixIndex].substring((int)prefixStart,(int)prefixEnd+1);
			
			for(int i = 0;i < prefix.length() && i < part.length();i++)
			{
				if(prefix.charAt(i) == part.charAt(i))
				{
					prefixNew = prefixNew + part.charAt(i);
					partLength--;
					
				}
				else
				{
					break;
				}
			}
			
			if(prefixNew.equals(prefix) || partLength == 0)
			{
				if (partLength != 0)
				{
					part = part.substring(prefix.length(), part.length());
				}
				
				else
				{
					if (ptr.firstChild != null)
					{
						ptr = ptr.firstChild;
						addList(ptr,wordList);
						break;
					}
					else
					{
						wordList.add(ptr);
						break;
					}
				}
				wordSearch(allWords,wordList,ptr,part,partLength);
				break;
			}
			
			else
			{
					partLength = placeLength;
					if (ptr.sibling == null)
					{
						break;
					}
					else
					{
						ptr = ptr.sibling;
					}
			}
		} while(true);
		
		return;
		
	}
	
	private static void addList (TrieNode ptr, ArrayList <TrieNode> wordList)
	{
		TrieNode sib = null;
		TrieNode child = null;
		if (ptr.sibling!= null)
		{
			sib = ptr.sibling;
			addList(sib,wordList);
		}
			
		if(ptr.firstChild!= null)
		{
			child = ptr.firstChild;
			addList(child,wordList);
		}
		else
		{
			wordList.add(ptr);
		}
	}
	
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }