package fr.uno.application.command.model.format

import fr.uno.domain.event._
import fr.uno.domain.model._

object GameEventJsonFormatter {
	import play.api.libs.json._

	implicit val gi_fmt = Json.format[GameId]

	implicit val numericValueCardFmt = Json.format[NumericCardValue]

	implicit val kickbackWriter = Writes[KickBackCardValue.type] { case KickBackCardValue => JsString("kickback") }
	implicit val kickbackReader = Reads[KickBackCardValue.type] {
		case JsString("kickback") => JsSuccess(KickBackCardValue)
		case _ => JsError("kickback card value reads error")
	}

	implicit val cardValueReader =
		__.read[NumericCardValue].map(x => x: CardValue).orElse(__.read[KickBackCardValue.type].map(x => x: CardValue))
	implicit val cardValueWriter = Writes[CardValue] {
		case n: NumericCardValue => Json.toJson(n)(numericValueCardFmt)
		case KickBackCardValue => Json.toJson(KickBackCardValue)(kickbackWriter)
	}

	implicit val colorReader = Reads[Color] {
		case JsString("red") => JsSuccess(Red)
		case JsString("blue") => JsSuccess(Blue)
		case JsString("green") => JsSuccess(Green)
		case JsString("yellow") => JsSuccess(Yellow)
		case _ => JsError("color json reads error")
	}
	implicit val colorWriter = Writes[Color] {
		case Red => JsString("red")
		case Blue => JsString("blue")
		case Green => JsString("green")
		case Yellow => JsString("yellow")
	}

	implicit val cardFmt = Json.format[Card]

	implicit val directionReader = Reads[Direction] {
		case JsString("clockwise") => JsSuccess(Clockwise)
		case JsString("counterclockwise") => JsSuccess(CounterClockwise)
		case _ => JsError("direction json reads error")
	}
	implicit val directionWriter = Writes[Direction] {
		case Clockwise => JsString("clockwise")
		case CounterClockwise => JsString("counterclockwise")
	}

	implicit val gameStartedFormat = Json.format[GameStarted]
	implicit val cardPlayedFormat = Json.format[CardPlayed]
	implicit val startGameAbordedFormat = Json.format[StartGameAborded]
	implicit val playerPlayedBadCardFormat = Json.format[PlayerPlayedBadCard]
	implicit val playerPlayedAtWrongTurnFormat = Json.format[PlayerPlayedAtWrongTurn]

	implicit val gameEventReader =
		__.read[GameStarted].map(x => x: GameEvent)
			.orElse(__.read[CardPlayed].map(x => x: GameEvent))
			.orElse(__.read[StartGameAborded].map(x => x: GameEvent))
			.orElse(__.read[PlayerPlayedBadCard].map(x => x: GameEvent))
			.orElse(__.read[PlayerPlayedAtWrongTurn].map(x => x: GameEvent))
	implicit val gameEventWriter = Writes[GameEvent] {
		case gs: GameStarted => Json.toJson(gs)(gameStartedFormat)
		case cp: CardPlayed => Json.toJson(cp)(cardPlayedFormat)
		case sga: StartGameAborded => Json.toJson(sga)(startGameAbordedFormat)
		case ppbc: PlayerPlayedBadCard => Json.toJson(ppbc)(playerPlayedBadCardFormat)
		case ppawt: PlayerPlayedAtWrongTurn => Json.toJson(ppawt)(playerPlayedAtWrongTurnFormat)
	}
}