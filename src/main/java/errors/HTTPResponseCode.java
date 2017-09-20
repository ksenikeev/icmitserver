package errors;

public enum HTTPResponseCode {
	// 1xx: Информационные коды - запрос получен, продолжается обработка	
	@Description("")
	@Code("")
	Continue,
	@Description("")
	@Code("")
	SwitchingProtocols,
	// 2xx: Успешные коды - действие было успешно получено, понято и обработано
	@Description("OK")
	@Code("200")
	OK,
	@Description("")
	@Code("")
	Created,
	@Description("")
	@Code("")
	Accepted,
	@Description("")
	@Code("")
	NonAuthoritativeInformation,
	@Description("")
	@Code("")
	NoContent,
	@Description("")
	@Code("")
	ResetContent,
	@Description("")
	@Code("")
	PartialContect,
	// 3xx: Коды перенаправления - для выполнения запроса должны быть предприняты дальнейшие действия
	@Description("")
	@Code("")
	MultipleChoices,
	@Description("")
	@Code("")
	MovedPermanently,
	@Description("")
	@Code("")
	MovedTemporarily,
	@Description("")
	@Code("")
	SeeOther,
	@Description("")
	@Code("")
	NotModified,
	@Description("")
	@Code("")
	UseProxy,
	// 4xx: Коды ошибок клиента - запрос имеет плохой синтаксис или не может быть выполнен
	@Description("")
	@Code("")
	BadRequest,
	@Description("")
	@Code("")
	Unauthorized,
	@Description("")
	@Code("")
	PaymentRequired,
	@Description("")
	@Code("")
	Forbidden,
	@Description("Not Found")
	@Code("404")
	NotFound,
	@Description("")
	@Code("")
	MethodNotAllowed,
	// 5xx: Коды ошибок сервера - сервер не в состоянии выполнить допустимый запрос
	InternalServerError,
	@Description("Not Implemented")
	@Code("501")
	NotImplemented,
	@Description("")
	@Code("")
	BadGateway,
	@Description("")
	@Code("")
	ServiceUnavailable,
	@Description("")
	@Code("")
	GatewayTimeout,
	@Description("")
	@Code("")
	HTTPVersionNotSupported;	
}
