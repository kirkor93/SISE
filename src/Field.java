public class Field 
{
	public FieldType MyFieldType;
	public boolean IsTrap = false;
	
	public Field()
	{
		MyFieldType = FieldType.NORMAL;
		IsTrap = false;
	}
	
	public Field(FieldType ft, boolean it)
	{
		MyFieldType = ft;
		IsTrap = it;
	}
}
