local msgPrefix = KEYS[1]
local userIdxPrefix = KEYS[2]
local msgId = ARGV[1]
local receiveUserId = ARGV[2]
local msgValue = ARGV[3]
local ttlSeconds = tonumber(ARGV[4])
local msgKey = msgPrefix .. msgId
local userListKey = userIdxPrefix .. receiveUserId

redis.call("SET", msgKey, msgValue)
if ttlSeconds and ttlSeconds > 0 then
    redis.call("EXPIRE", msgKey, ttlSeconds)
end
redis.call("RPUSH", userListKey, msgId)
if ttlSeconds and ttlSeconds > 0 then
    redis.call("EXPIRE", userListKey, ttlSeconds)
end

return 1
