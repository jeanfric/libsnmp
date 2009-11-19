require 'yaml'

complete_mib_list = {}
for file in ARGV
	mibs = []
	YAML::load_documents(File.open(file)){ |mib| mibs << mib }

	mibs.each do |e|
		e.each do |k,v|
			complete_mib_list[v.strip] = k.strip
		end
	end
end


h = complete_mib_list

sorted = h.keys.sort_by {|s| s.to_s}.map {|key| [key, h[key]] }


puts '<?xml version="1.0" encoding="utf-8"?>'
puts '<resources>'

puts '  <string-array name="object_identifiers_ids">'
sorted.each do |k,v|
	    puts "    <item>#{k}</item>"
end
puts '  </string-array>'

puts '  <string-array name="object_identifiers_names">'
sorted.each do |k,v|
	    puts "    <item>#{v}</item>"
end
puts '  </string-array>'

puts '</resources>'
