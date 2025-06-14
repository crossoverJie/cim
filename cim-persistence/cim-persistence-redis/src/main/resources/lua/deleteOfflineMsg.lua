local msgPrefix = KEYS[1]
local userIdxPrefix = KEYS[2]

local userId = ARGV[1]
local userListKey = userIdxPrefix .. userId

for i = 2, #ARGV do
    local msgKey = msgPrefix .. ARGV[i]
    redis.log(redis.LOG_NOTICE, "msgKey: ".. msgKey)
    redis.call("DEL", msgKey)

    redis.call("LREM", userListKey, 0, ARGV[i])
end