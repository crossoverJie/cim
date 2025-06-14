local userId = ARGV[1]
local rangeSize = ARGV[2]
local msgPrefix = KEYS[1]
local userIdxPrefix = KEYS[2]
local userListKey = userIdxPrefix .. userId

local ids = redis.call('LRANGE', userListKey, 0, rangeSize - 1)

local result = {}
for i, id in ipairs(ids) do
  local msgKey = msgPrefix .. id
  local serializedMsg = redis.call('GET', msgKey)

  if serializedMsg then
    table.insert(result, serializedMsg)
  end
end
return result