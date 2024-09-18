
redis.call('DEL', KEYS[1])

redis.call('SREM', ARGV[1], ARGV[2])

