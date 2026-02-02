#!/bin/bash
set -euo pipefail

# ---- Config (good Zero 2 W defaults) ----
DEV="${DEV:-/dev/video0}"

# Latency-first: start here, then tune
WIDTH="${WIDTH:-640}"
HEIGHT="${HEIGHT:-480}"
FPS="${FPS:-20}"

PORT="${PORT:-8554}"
PATHNAME="${PATHNAME:-/}"

# Server-side caching: keep small
LIVE_CACHING_MS="${LIVE_CACHING_MS:-50}"
SOUT_RTP_CACHING_MS="${SOUT_RTP_CACHING_MS:-0}"

# Watchdog
RESTART_DELAY_SEC="${RESTART_DELAY_SEC:-1}"
LOG="${LOG:-/tmp/usb_cam_stream.log}"
# ----------------------------------------

cleanup() {
  echo "[INFO] Cleaning up..." | tee -a "$LOG"
  pkill -P $$ 2>/dev/null || true
}
trap cleanup EXIT INT TERM

require_cmd() { command -v "$1" >/dev/null 2>&1 || { echo "[ERROR] Missing: $1" | tee -a "$LOG"; exit 1; }; }
require_cmd cvlc
require_cmd v4l2-ctl

HOST_IP="$(hostname -I | awk '{print $1}')"
echo "[INFO] Host IP: $HOST_IP" | tee -a "$LOG"
echo "[INFO] Device: $DEV  ${WIDTH}x${HEIGHT}@${FPS}fps  RTSP: rtsp://${HOST_IP}:${PORT}${PATHNAME}" | tee -a "$LOG"

echo "[INFO] Checking camera formats..." | tee -a "$LOG"
v4l2-ctl -d "$DEV" --list-formats-ext 2>/dev/null | tee -a "$LOG" | grep -q "MJPG" || {
  echo "[ERROR] Camera does not advertise MJPG. Latency will be poor on YUYV." | tee -a "$LOG"
  exit 1
}

# Best-effort set format + fps
v4l2-ctl -d "$DEV" --set-fmt-video=width="$WIDTH",height="$HEIGHT",pixelformat=MJPG 2>/dev/null || true
v4l2-ctl -d "$DEV" --set-parm="$FPS" 2>/dev/null || true

while true; do
  if [[ ! -e "$DEV" ]]; then
    echo "[WARN] $DEV not present. Waiting..." | tee -a "$LOG"
    sleep "$RESTART_DELAY_SEC"
    continue
  fi

  echo "[INFO] Starting VLC RTSP MJPEG stream..." | tee -a "$LOG"

  cvlc -vvv -I dummy "v4l2://${DEV}" \
    --v4l2-width="$WIDTH" \
    --v4l2-height="$HEIGHT" \
    --v4l2-fps="$FPS" \
    --v4l2-chroma=MJPG \
    --live-caching="$LIVE_CACHING_MS" \
    --sout-rtp-caching="$SOUT_RTP_CACHING_MS" \
    --sout "#rtp{sdp=rtsp://0.0.0.0:${PORT}${PATHNAME}}" \
    --no-sout-all --sout-keep \
    >>"$LOG" 2>&1 || true

  echo "[WARN] VLC exited. Restarting in ${RESTART_DELAY_SEC}s..." | tee -a "$LOG"
  sleep "$RESTART_DELAY_SEC"
done
