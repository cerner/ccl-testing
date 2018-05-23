drop		program		normal_script		go
create		program		normal_script
/**
		This		is		an		example		of		a		normal		script.
		It		has		arguments		and		a		bound		transaction.
		
		@boundTransaction		1337
		@arg		This		is		the		first		argument.
		@arg		This		is		the		second		argument.
		*/

				declare		not_subroutine		=		i4		with		protect,		noconstant(0)
				declare		get_birth_dt_tm(person_id		=		f8,		
																birth_dt_tm		=		dq8(REF))		=		null
				declare		get_last_name(person_id		=		f8)		=		vc		with		protect
				
				/**
						This		is		the		request		record		structure.

						@request		
						@field		first_list		The		first		list
								@field		first_list_ind		Indicator		in		the		first		list
				@field		second_list		The		second		list
								@field		second_list_vc		The		name		of		the		member		of		the		second		list
												@codeSet		387		The		code		set		for		second_list_vc
				@field		shared_name		The		first		instance		of		a		shared-name		field
								@value		1		Yahoo!
								@value		2		Too		blue!
								@value		3		This		time's		the		charm.
				@field		shared_name_list
								A		list		that		contains		a		member		with		a		shared		name
								@field		shared_name		The		second		field		that		has		a		shared		name
				*/
				record		request		(
								1		first_list[*]
												2		first_list_ind		=		i2
								1		second_list[*]
												2		second_list_vc		=		vc
								1		shared_name		=		c24
								1		shared_name_list[31]
												2		shared_name		=		f8
				)
				
				/**
						@reply
								What-what?
						@field		success_ind		The		success		indicator
				*/
				record		reply		(
								1		success_ind		=		i2
				)
				
				/**
						*		This		really		doesn't		have		to		do		with		anything
						*		@field		char_field
						*						This		is		the		character		field
						*/
				record		med_data		(
								1		char_field		=		c1
				)

				/**
						*		This		is		a		simple		subroutine		that		has		its		documentation		wrapped		in		stars.
						*		@param		person_id		The		ID		of		the		person
						*		@param		birth_dt_tm
						*						The		date		and		time		of		the		birth		that		is		retrieved.
						*/
				subroutine		get_birth_dt_tm(person_id,		birth_dt_tm)
								select		p.birth_dt_tm		from		people		p
								where		p.person_id		=		person_id
								detail
												birth_dt_tm		=		p.birth_dt_tm
								with		nocounter
				end
				
				/**
						This		is		a		subroutine		with		no		encasing		stars.
						It		also		has		scope!
						@param		person_id
								The		ID		of		the		person		whose		name		is		to		be		retrieved.
						@returns
								The		last		name		of		the		person		found
				*/
				subroutine		get_last_name(person_id)
								declare		last_name		=		vc
								select		p.last_name
								from		people		p
								where		p.person_id		=		person_id
								detail
												last_name		=		p.last_name
								with		nocounter
								
								return(last_name)
				end
				
				/**
						This		subroutine		has		no		declaration,		so		the		information		about		it
						will		be		severely		limited.
						@param		person_id		The		ID		of		the		person		whose		SSN		is		to		be		retrieved.
						@returns		The		given		person's		SSN.
						*/
				subroutine		get_ssn(person_id)
								declare		ssn		=		vc
								select		p.ssn
								from		people		p
								where		p.person_id		=		person_id
								detail
												ssn		=		p.ssn
								with		nocounter
								
								return(ssn)
				end

end
go
