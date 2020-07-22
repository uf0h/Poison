package me.ufo.poison.shared;

public enum PoisonAction {

  // DETATCHED <includes all>
  PING,
  SEND_ALL_SERVER_DATA,
  // BUNGEE
  RECEIVE_HUB,
  PLAYER_SEND, // PLAYER_SEND[to hub]

  // HUB
  JOIN_QUEUE,
  LEAVE_QUEUE,
  JOINED_QUEUE,
  LEFT_QUEUE,

  // BUKKIT
  REQUEST_HUB,

}
