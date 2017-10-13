package animeLinkSorter;

public class Entry {
	String name;
	Float value;
	String image;
	String title;
	
	Entry(String s, Float f, String i){
		name=s;value=f;image=i;
		title=name.substring(name.lastIndexOf("/")+1);
		title=title.replace('_', ' ');
	}
	
	
	
//	Entry(String s, Float f ){
//		name=s;value=f;image="";
//	}
}
