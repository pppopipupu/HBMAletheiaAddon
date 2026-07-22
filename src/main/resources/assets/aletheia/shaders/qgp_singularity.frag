uniform sampler2D texture;
uniform float time;
uniform int state;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec2 center = vec2(0.5, 0.5);
    vec2 dir = uv - center;
    float dist = length(dir);

    float wave = sin(dist * 20.0 - time * 5.0) * 0.03;
    vec2 distUv = uv + normalize(dir) * wave;

    vec4 color = texture2D(texture, distUv);

    vec3 tint = vec3(0.0);
    if (state == 1) {
        tint = vec3(0.1, 0.6, 1.0);
    } else if (state == 2) {
        tint = vec3(0.9, 0.1, 0.2);
    } else {
        tint = vec3(0.8, 0.2, 1.0);
    }

    float glow = smoothstep(0.5, 0.0, dist);
    color.rgb = mix(color.rgb, tint, 0.6) + tint * glow * 0.5;
    color.a = clamp(glow * 1.5, 0.0, 1.0);

    gl_FragColor = color;
}
