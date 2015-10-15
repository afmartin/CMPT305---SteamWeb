package cmpt305.lab3.stucture;

public interface Filter<T>{
	public static final Filter ANY = new Filter(){
		@Override
		public boolean accept(Object t){
			return true;
		}
	},
			NONE = new Filter(){
				@Override
				public boolean accept(Object t){
					return false;
				}
			};

	public boolean accept(T t);
}
